package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;

public class CommonProxy {
    public CommonProxy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    public void handleCombatModePacketServer(NetworkEvent.Context ctx, CombatModePacket msg) {
        if(ctx.getSender() != null)
            ((CombatModeAccessor) ctx.getSender()).setCombatMode(msg.combatMode());
    }

    public void handleCombatModePacketClient(NetworkEvent.Context context, CombatModePacket msg) {

    }
}
