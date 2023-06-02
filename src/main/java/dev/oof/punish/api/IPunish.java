package dev.oof.punish.api;

import java.util.UUID;

public interface IPunish {

    int getId();
    IType getType();
    UUID getUuid();
    String getPunishedName();
    UUID getStaffUuid();
    String getReason();
    long getTimePunished();
    long getDuration();
    boolean isRemoved();
    boolean isActive();
    void setId(int id);
    void setRemoved(boolean removed);
}
