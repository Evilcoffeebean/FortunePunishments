package dev.oof.punish.cmd;

import dev.oof.punish.api.IManager;
import dev.oof.punish.base.PlayerPunishment;
import dev.oof.punish.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {

    private final IManager manager;

    public KickCommand(IManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // block console
            sender.sendMessage(MessageUtil.error("Only players can use this command."));
            return false;
        }

        Player staff = (Player) sender;

        if (!staff.hasPermission("punisher.command.kick")) {
            sender.sendMessage(MessageUtil.error("You do not have permission to use this command."));
        }

        if (args.length < 2) {
            staff.sendMessage(MessageUtil.error(
                    "Incorrect Usage: " + ChatColor.WHITE + "/kick <player> <reason>"
            ));
            return false;
        }

        Player target = Bukkit.getPlayerExact(args[0]); // target player by name
        if (target == null || !target.isOnline()) {
            // player not found
            staff.sendMessage(MessageUtil.error("That player can't be found."));
            return false;
        }

        // build reason
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String reason = sb.toString().trim();

        // punish player
        manager.savePunishment(new PlayerPunishment(
                manager.getPunishmentType("KICK"),
                target.getUniqueId(),
                target.getName(),
                staff.getUniqueId(),
                reason,
                System.currentTimeMillis(),
                -1L,
                false
        ));

        return false;
    }
}
