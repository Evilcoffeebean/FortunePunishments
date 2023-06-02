package dev.oof.punish.type;

import dev.oof.punish.api.IPunish;
import dev.oof.punish.api.IType;
import dev.oof.punish.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KickType implements IType {

    @Override
    public String getId() {
        return "KICK";
    }

    @Override
    public void onPunish(IPunish punishment) {
        // player will be online, as it's a kick
        Player player = Bukkit.getPlayer(punishment.getUuid());

        // notify staff member
        Player staff = Bukkit.getPlayer(punishment.getStaffUuid());
        staff.sendMessage(MessageUtil.format(
                ChatColor.YELLOW + player.getName()
                        + ChatColor.GRAY + " was kicked with reason: "
                        + ChatColor.WHITE + punishment.getReason()
                        + ChatColor.GRAY + "."
        ));

        // kick
        player.kickPlayer(
                MessageUtil.PREFIX + "\n"
                        + ChatColor.GRAY + "You have been kicked from the server.\n\n"
                        + "Reason: " + ChatColor.WHITE + punishment.getReason() + "\n"
                        + ChatColor.GRAY + "Staff Member: " + ChatColor.YELLOW + staff.getName()
        );
    }

    @Override
    public String onJoin(IPunish punishment) {
        // joins are not affected by kicks
        return null;
    }

    @Override
    public boolean onChat(IPunish punishment) {
        // chat is not affected by kicks
        return true;
    }
}
