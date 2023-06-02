package dev.oof.punish.type;

import dev.oof.punish.api.IPunish;
import dev.oof.punish.api.IType;
import dev.oof.punish.util.MessageUtil;
import dev.oof.punish.util.NameFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormat;

public class BanType implements IType {

    @Override
    public String getId() {
        return "BAN";
    }

    @Override
    public void onPunish(IPunish punishment) {
        // player may not be online, as staff can punish offline players
        Player player = Bukkit.getPlayer(punishment.getUuid());
        String playerName = player != null && player.isOnline() ? player.getName() : NameFetcher.getNameOf(punishment.getUuid());

        boolean permanent = punishment.getTimePunished() < 0;
        long duration = punishment.getDuration();

        // notify staff member
        // e.g. SomePlayer was banned for 10 minutes with reason: fly hacking.
        Player staffPlayer = Bukkit.getPlayer(punishment.getStaffUuid());
        String staffPeriod = permanent ? ChatColor.YELLOW + "permanently" : "for " +
                ChatColor.YELLOW + PeriodFormat.getDefault().print(new Duration(duration).toPeriod());
        staffPlayer.sendMessage(MessageUtil.format(
                ChatColor.YELLOW + playerName + ChatColor.GRAY + " was banned " + staffPeriod +
                        ChatColor.GRAY + " with reason: " + ChatColor.WHITE + punishment.getReason() +
                        ChatColor.GRAY + "."
        ));

        if (player != null && player.isOnline()) {
            long remaining = duration - (System.currentTimeMillis() - punishment.getTimePunished());
            player.kickPlayer(
                    MessageUtil.PREFIX + "\n"
                            + ChatColor.GRAY + "You have been banned from the server.\n\n"
                            + "Reason: " + ChatColor.WHITE + punishment.getReason() + "\n"
                            + ChatColor.GRAY + "Staff Member: " + ChatColor.YELLOW + staffPlayer.getName() + "\n"
                            + ChatColor.GRAY + "Time Remaining: " + ChatColor.YELLOW + (permanent ?
                            "Forever" : PeriodFormat.getDefault().print(new Duration(remaining).toPeriod()))
            );
        }
    }

    @Override
    public String onJoin(IPunish punishment) {
        String time = punishment.getDuration() < 0 ? "Forever" : PeriodFormat.getDefault()
                .print(new Duration(punishment.getDuration() - (System.currentTimeMillis() -
                        punishment.getTimePunished())).toPeriod());

        return MessageUtil.PREFIX + "\n"
                + ChatColor.GRAY + "You are banned from the server.\n\n"
                + "Reason: " + ChatColor.WHITE + punishment.getReason() + "\n"
                + ChatColor.GRAY + "Staff Member: " + ChatColor.YELLOW + NameFetcher.getNameOf(punishment.getStaffUuid()) + "\n"
                + ChatColor.GRAY + "Time Remaining: " + ChatColor.YELLOW + time;
    }

    @Override
    public boolean onChat(IPunish punishment) {
        // chat is not affected by bans
        return true;
    }
}
