package dev.oof.punish.util;

import org.bukkit.ChatColor;

public class MessageUtil {

    public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Punisher" + ChatColor.DARK_GRAY + "]";

    public static final String format(String content) {
        return PREFIX + " " + ChatColor.GRAY + content;
    }

    public static final String error(String content) {
        return PREFIX + " " + ChatColor.RED + content;
    }

}
