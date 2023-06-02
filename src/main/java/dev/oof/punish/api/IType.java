package dev.oof.punish.api;

public interface IType {

    String getId();
    void onPunish(IPunish punishment);
    String onJoin(IPunish punishment);
    boolean onChat(IPunish punishment);
}
