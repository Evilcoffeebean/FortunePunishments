package dev.oof.punish.api;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.Set;
import java.util.UUID;

public interface IManager {

    ListenableFuture<Set<IPunish>> loadPunishments(UUID player);
    Set<IPunish> getCachedPunishments(UUID player);
    boolean hasCachedPunishments(UUID player);
    ListenableFuture<Boolean> savePunishment(IPunish punishment);
    IType getPunishmentType(String id);
    IPunish getPunishment(int id);
}
