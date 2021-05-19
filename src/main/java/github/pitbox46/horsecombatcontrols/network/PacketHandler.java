package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

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
                (msg, pb) -> pb.writeEnumValue(msg.type),
                pb -> new EmptyPacket(pb.readEnumValue(EmptyPacket.Type.class)),
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
                    pb.writeUniqueId(msg.uuid);
                    pb.writeBoolean(msg.bool);
                },
                pb -> new UUIDPacket(pb.readUniqueId(), pb.readBoolean()),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> HorseCombatControls.PROXY.handleToggleMode(ctx.get(), msg.uuid, msg.bool));
                    ctx.get().setPacketHandled(true);
                });
    }
}
