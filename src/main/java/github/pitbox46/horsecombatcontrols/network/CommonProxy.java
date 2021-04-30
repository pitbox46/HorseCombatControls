package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class CommonProxy {
    public CommonProxy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    //Server
    public void handleToggleMode(NetworkEvent.Context ctx) {
        if(ctx.getSender() != null) {
            ((CombatModeAccessor) ctx.getSender()).toggleCombatMode();
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new UUIDPacket(ctx.getSender().getUniqueID()));
        }
    }

    //Client
    public void handleToggleMode(NetworkEvent.Context ctx, UUID uuid) {
    }
}
