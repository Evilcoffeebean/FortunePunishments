package dev.oof.punish.base;

import dev.oof.punish.api.IPunish;
import dev.oof.punish.api.IType;

import java.util.UUID;

public class PlayerPunishment implements IPunish {

    private int id; // non-final so it can be set after db has auto-generated id
    private final IType type;
    private final UUID uuid;
    private final String punishedName;
    private final UUID staffUuid;
    private final String reason;
    private final long timePunished;
    private final long duration;
    private boolean removed;

    public PlayerPunishment(int id, IType type, UUID uuid, String punishedName, UUID
            staffUuid, String reason, long timePunished, long duration, boolean removed) {
        this.id = id;
        this.type = type;
        this.uuid = uuid;
        this.punishedName = punishedName;
        this.staffUuid = staffUuid;
        this.reason = reason;
        this.timePunished = timePunished;
        this.duration = duration;
        this.removed = removed;
    }

    public PlayerPunishment(IType type, UUID uuid, String punishedName, UUID staffUuid,
                            String reason, long timePunished, long duration, boolean removed) {
        this(-1, type, uuid, punishedName, staffUuid, reason, timePunished, duration, removed);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public IType getType() {
        return type;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getPunishedName() {
        return punishedName;
    }

    @Override
    public UUID getStaffUuid() {
        return staffUuid;
    }

    @Override
    public String getReason() {
        return reason;
    }

    @Override
    public long getTimePunished() {
        return timePunished;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public boolean isActive() {
        return !isRemoved() && (getDuration() == -1L || System.currentTimeMillis() < (getTimePunished() + getDuration()));
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}
