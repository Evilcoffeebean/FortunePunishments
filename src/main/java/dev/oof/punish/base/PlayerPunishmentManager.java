package dev.oof.punish.base;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import dev.oof.punish.PunishmentsCore;
import dev.oof.punish.api.IPunish;
import dev.oof.punish.api.IManager;
import dev.oof.punish.api.IType;
import dev.oof.punish.data.PunishmentDatabase;
import dev.oof.punish.type.BanType;
import dev.oof.punish.type.KickType;
import dev.oof.punish.type.MuteType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

public class PlayerPunishmentManager implements IManager, Listener {

    private final PunishmentsCore plugin;
    private final PunishmentDatabase database;
    private final List<IType> types;
    private final Map<UUID, Set<IPunish>> punishments;

    public PlayerPunishmentManager(PunishmentsCore plugin) {
        this.plugin = plugin;
        this.database = new PunishmentDatabase(this, plugin);

        this.types = new ArrayList<>();
        this.types.add(new BanType());
        this.types.add(new KickType());
        this.types.add(new MuteType());

        this.punishments = new ConcurrentHashMap<>();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        ListenableFuture<Set<IPunish>> fut = loadPunishments(event.getPlayer().getUniqueId());
        fut.addListener(() -> {
            try {
                // load punishments; they are cached locally also
                Set<IPunish> puns = fut.get();

                // for each punishment, call the join event and disallow login if necessary
                puns.stream().filter(IPunish::isActive).forEach(p -> {
                    String r = p.getType().onJoin(p);
                    if (r != null) {
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, r);
                    }
                });
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, r -> plugin.getServer().getScheduler().runTask(plugin, r));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (!punishments.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        punishments.get(event.getPlayer().getUniqueId()).stream().filter(IPunish::isActive)
                .forEach(p -> {
                    // cancel the chat if punishment type chat even returns false
                    event.setCancelled(event.isCancelled() || !p.getType().onChat(p));
                });
    }

    @Override
    public ListenableFuture<Set<IPunish>> loadPunishments(UUID player) {
        SettableFuture<Set<IPunish>> fut = SettableFuture.create();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // load from database; cache locally
            Set<IPunish> puns = database.loadPunishments(player);
            punishments.put(player, puns);

            // complete future
            fut.set(puns);
        });

        return fut;
    }

    @Override
    public Set<IPunish> getCachedPunishments(UUID player) {
        return punishments.get(player);
    }

    @Override
    public boolean hasCachedPunishments(UUID player) {
        return getCachedPunishments(player) != null;
    }

    @Override
    public ListenableFuture<Boolean> savePunishment(IPunish punishment) {
        SettableFuture<Boolean> fut = SettableFuture.create();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            // save in database
            fut.set(database.savePunishment(punishment));

            // call punishment received event
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    punishment.getType().onPunish(punishment));
        });

        return fut;
    }

    @Override
    public IType getPunishmentType(String id) {
        return types.stream().filter(t -> t.getId().equals(id)).findAny().orElse(null);
    }

    @Override
    public IPunish getPunishment(int id) {
        if (id < 0) {
            // ignore non-auto-generated ids
            return null;
        }

        return punishments.values().stream().flatMap(Collection::stream).filter(p -> p.getId() == id)
                .findFirst().orElse(null);
    }
}
