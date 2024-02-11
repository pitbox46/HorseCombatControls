package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

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
                CombatModePacket.class,
                (msg, pb) -> pb.writeBoolean(msg.combatMode()),
                pb -> new CombatModePacket(pb.readBoolean()),
                (msg, ctx) -> {
                    ctx.get().enqueueWork(() -> {
                        if(ctx.get().getDirection().getReceptionSide() == LogicalSide.CLIENT)
                            HorseCombatControls.PROXY.handleCombatModePacketClient(ctx.get(), msg);
                        else
                            HorseCombatControls.PROXY.handleCombatModePacketServer(ctx.get(), msg);
                    });
                    ctx.get().setPacketHandled(true);
                });
    }
}