package dev.oof.punish;

import dev.oof.punish.api.IManager;
import dev.oof.punish.base.PlayerPunishmentManager;
import dev.oof.punish.cmd.*;
import org.bukkit.plugin.java.JavaPlugin;

public class PunishmentsCore extends JavaPlugin {

    /*
        deathibring was here
     */

    @Override
    public void onEnable() {
        // save default config file to data folder
        saveDefaultConfig();

        // instantiate punishment manager
        IManager manager = new PlayerPunishmentManager(this);

        // commands
        getCommand("kick").setExecutor(new KickCommand(manager));
        getCommand("ban").setExecutor(new BanCommand(manager));
        getCommand("mute").setExecutor(new MuteCommand(manager));
        getCommand("phistory").setExecutor(new PunishmentHistoryCommand(manager));
        getCommand("premove").setExecutor(new PunishmentRemoveCommand(manager));
    }
}
