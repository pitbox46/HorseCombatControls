package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "3.2.1";
    public static SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("reroll","main"),
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
                    ctx.get().enqueueWork(() -> HorseCombatControls.PROXY.handleToggleMode(ctx.get()));
                    ctx.get().setPacketHandled(true);
                });
    }
}
