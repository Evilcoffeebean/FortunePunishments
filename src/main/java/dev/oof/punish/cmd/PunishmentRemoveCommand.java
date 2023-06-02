package dev.oof.punish.cmd;

import com.google.common.util.concurrent.ListenableFuture;
import dev.oof.punish.api.IPunish;
import dev.oof.punish.api.IManager;
import dev.oof.punish.util.MessageUtil;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ExecutionException;

public class PunishmentRemoveCommand implements CommandExecutor {

    private final IManager manager;

    public PunishmentRemoveCommand(IManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("punisher.command.remove")) {
            sender.sendMessage(MessageUtil.error("You do not have permission to use this command."));
        }

        if (args.length != 1) {
            sender.sendMessage(MessageUtil.format(
                    "Incorrect Usage: " + ChatColor.WHITE + "/premove <id>"
            ));
            sender.sendMessage(MessageUtil.format(
                    "Use " + ChatColor.YELLOW + "/phistory <player> "
                            + ChatColor.GRAY + "to show ids of punishments."
            ));
            return false;
        }

        if (!NumberUtils.isNumber(args[0])) {
            sender.sendMessage(MessageUtil.error("That is not a valid punishment id."));
            return false;
        }

        int id = Integer.parseInt(args[0]);
        IPunish punishment = manager.getPunishment(id);

        if (punishment == null) {
            sender.sendMessage(MessageUtil.error(
                    "That punishment can't be found. Did you use "
                            + ChatColor.WHITE + "/phistory <player> " + ChatColor.RED + "to load it first?"
            ));
        }

        // remove punishment
        punishment.setRemoved(true);

        ListenableFuture<Boolean> future = manager.savePunishment(punishment);
        future.addListener(() -> {
            try {
                if (future.get()) {
                    sender.sendMessage(MessageUtil.format(
                            "Successfully removed punishment " + ChatColor.YELLOW + "#"
                                    + punishment.getId() + ChatColor.GRAY + "."
                    ));
                }
                else {
                    sender.sendMessage(MessageUtil.error(
                            "There was an error removing that punishment."
                    ));
                }
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                sender.sendMessage(MessageUtil.error(
                        "There was an error removing that punishment."
                ));
            }
        }, Runnable::run);

        return false;
    }
}
