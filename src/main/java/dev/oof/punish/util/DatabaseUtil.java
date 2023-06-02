package dev.oof.punish.util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import javax.sql.DataSource;

public class DatabaseUtil {

    public static DataSource generateDataSource(FileConfiguration config) {
        ConfigurationSection cs = config.getConfigurationSection("database");
        BasicDataSource bds = new BasicDataSource();

        bds.setDriverClassName("com.mysql.jdbc.Driver");
        bds.setUrl(
                "jdbc:mysql://" + cs.getString("host") + ":" + cs.getInt("port") + "/" + cs.getString("database")
        );
        bds.setUsername(cs.getString("username"));
        bds.setPassword(cs.getString("password"));

        return bds;
    }
}
