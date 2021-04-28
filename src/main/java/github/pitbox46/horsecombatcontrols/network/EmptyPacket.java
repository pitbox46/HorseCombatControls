package github.pitbox46.horsecombatcontrols.network;

public class EmptyPacket {
    public enum Types {
        TOGGLE_MODE,
        REQUEST_MODE,
    }
    public final EmptyPacket.Types type;
    public EmptyPacket(EmptyPacket.Types type) {
        this.type = type;
    }
}
