package dev.oof.punish.cmd;

import dev.oof.punish.api.IManager;
import dev.oof.punish.base.PlayerPunishment;
import dev.oof.punish.util.MessageUtil;
import dev.oof.punish.util.NameFetcher;
import dev.oof.punish.util.UUIDFetcher;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MuteCommand implements CommandExecutor {

    private final IManager manager;

    public MuteCommand(IManager manager) {
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

        if (!staff.hasPermission("punisher.command.mute")) {
            sender.sendMessage(MessageUtil.error("You do not have permission to use this command."));
        }

        if (args.length < 3) {
            staff.sendMessage(MessageUtil.error(
                    "Incorrect Usage: " + ChatColor.WHITE + "/mute <player> <hours | -1 (permanent)> <reason>"
            ));
            return false;
        }

        // find target player online or offline
        Player targetPlayer = Bukkit.getPlayerExact(args[0]);
        UUID targetUuid = targetPlayer != null && targetPlayer.isOnline() ? targetPlayer
                .getUniqueId() : UUIDFetcher.getUUIDOf(args[0]);
        if (targetUuid == null) {
            staff.sendMessage(MessageUtil.error("That player can't be found online or offline."));
            return false;
        }

        String targetName = targetPlayer != null && targetPlayer.isOnline() ? targetPlayer
                .getName() : NameFetcher.getNameOf(targetUuid);

        // time length of punishment
        if (!NumberUtils.isNumber(args[1])) {
            staff.sendMessage(MessageUtil.error("That is not a valid number of hours, or -1 for " +
                    "permanent."));
            return false;
        }

        long duration;
        int num = Integer.parseInt(args[1]);
        if (num == -1) {
            duration = -1L;
        }
        else {
            duration = num * 60 * 60 * 1000;
        }

        // build reason
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String reason = sb.toString().trim();

        // punish player
        manager.savePunishment(new PlayerPunishment(
                manager.getPunishmentType("MUTE"),
                targetUuid,
                targetName,
                staff.getUniqueId(),
                reason,
                System.currentTimeMillis(),
                duration,
                false
        ));

        return false;
    }
}
