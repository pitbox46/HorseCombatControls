package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "3.2.1";
    public static SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("horsecombatcontrols","main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);
    private static int ID = 0;

    public static void init() {
        CHANNEL.registerMessage(
                ID++,
                EmptyPacket.class,
                (msg, pb) -> pb.writeEnum(msg.type),
                pb -> new EmptyPacket(pb.readEnum(EmptyPacket.Type.class)),
                (msg, ctx) -> {
                    switch(msg.type) {
                        case TOGGLE_MODE:
                            ctx.get().enqueueWork(() -> HorseCombatControls.PROXY.handleToggleMode(ctx.get()));
                            break;
                        case SYNC_MODE:
                            ctx.get().enqueueWork(() -> HorseCombatControls.PROXY.handleSyncMode(ctx.get()));
                            break;
                        default:
                            throw new Error("You forgot to create a new case for the new enum!");
                    }
                    ctx.get().setPacketHandled(true);
                });
        CHANNEL.registerMessage(
                ID++,
                UUIDPacket.class,
                (msg, pb) -> {
                    pb.writeUUID(msg.uuid);
                    pb.writeBoolean(msg.bool);
                },
                pb -> new UUIDPacket(pb.readUUID(), pb.readBoolean()),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> HorseCombatControls.PROXY.handleToggleMode(ctx.get(), msg.uuid, msg.bool));
                    ctx.get().setPacketHandled(true);
                });
    }
}
