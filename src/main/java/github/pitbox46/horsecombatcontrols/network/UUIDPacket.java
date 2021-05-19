package github.pitbox46.horsecombatcontrols.network;

import java.util.UUID;

public class UUIDPacket {
    public final UUID uuid;
    public final boolean bool;
    public UUIDPacket(UUID uuid, boolean bool) {
        this.uuid = uuid;
        this.bool = bool;
    }
}
