package dev.oof.punish.data;

import dev.oof.punish.PunishmentsCore;
import dev.oof.punish.api.IPunish;
import dev.oof.punish.api.IManager;
import dev.oof.punish.base.PlayerPunishment;
import dev.oof.punish.util.DatabaseUtil;
import dev.oof.punish.util.data.SelectQueryCallable;
import dev.oof.punish.util.data.UpdateQueryCallable;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PunishmentDatabase {

    private static final String LOAD_PUNISHMENTS = "SELECT * FROM punishments WHERE uuid = ?";

    private static final String SAVE_PUNISHMENT = "INSERT INTO punishments (uuid, " +
            "punishedName, type, staff, reason, timePunished, duration, removed) VALUES (?, ?, ?," +
            " ?, ?, ?, ?, ?)";

    private static final String UPDATE_PUNISHMENT = "UPDATE punishments SET removed = ? WHERE id " +
            "= ?";

    private final IManager manager;
    private final PunishmentsCore plugin;
    private final DataSource database;

    public PunishmentDatabase(IManager manager, PunishmentsCore plugin) {
        this.manager = manager;
        this.plugin = plugin;

        // generate data source and save
        this.database = DatabaseUtil.generateDataSource(plugin.getConfig());
    }

    public boolean savePunishment(IPunish punishment) {
        UpdateQueryCallable update;

        if (punishment.getId() == -1) {
            // new punishment
            update = new UpdateQueryCallable(
                    database, SAVE_PUNISHMENT, new Object[]{
                            punishment.getUuid().toString(),
                            punishment.getPunishedName(),
                            punishment.getType().getId(),
                            punishment.getStaffUuid().toString(),
                            punishment.getReason(),
                            punishment.getTimePunished(),
                            punishment.getDuration(),
                            punishment.isRemoved()
                    },
                    results -> {
                        try {
                            // retrieve auto-generated id
                            results.first();
                            punishment.setId(results.getInt(1));
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
            );
        }
        else {
            // existing punishment
            update = new UpdateQueryCallable(
                    database, UPDATE_PUNISHMENT, new Object[]{
                            punishment.isRemoved(),
                            punishment.getId()
                    },
                    results -> {}
            );
        }

        try {
            update.call();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Set<IPunish> loadPunishments(UUID uuid) {
        SelectQueryCallable<Set<IPunish>> select = new SelectQueryCallable<>(
                database,
                LOAD_PUNISHMENTS,
                new Object[]{ uuid.toString() },
                results -> {
                    // handle result set of loaded punishments
                    Set<IPunish> punishments = new HashSet<>();

                    try {
                        while (results.next()) {
                            IPunish p = new PlayerPunishment(
                                    results.getInt("id"),
                                    manager.getPunishmentType("type"),
                                    UUID.fromString(results.getString("uuid")),
                                    results.getString("punishedName"),
                                    UUID.fromString(results.getString("staff")),
                                    results.getString("reason"),
                                    results.getLong("timePunished"),
                                    results.getLong("duration"),
                                    results.getBoolean("removed")
                            );

                            punishments.add(p);
                        }
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }

                    return punishments;
                }
        );

        try {
            return select.call();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
