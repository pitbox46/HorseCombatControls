package github.pitbox46.horsecombatcontrols.network;

public class EmptyPacket {
    public enum Type {
        TOGGLE_MODE,
        SYNC_MODE
    }
    public Type type;
    public EmptyPacket(Type type) {
        this.type = type;
    }
}
