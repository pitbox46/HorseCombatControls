package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import github.pitbox46.horsecombatcontrols.Config;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

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
            if(Config.LOCK_COMBAT_MODE.get()) {
                ctx.getSender().displayClientMessage(new TranslatableComponent("message.horsecombatcontrols.locked"), true);
                return;
            }
            boolean flag = ((CombatModeAccessor) ctx.getSender()).toggleCombatMode();
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new UUIDPacket(ctx.getSender().getUUID(), flag));
        }
    }

    public void handleSyncMode(NetworkEvent.Context ctx) {
        if(ctx.getSender() != null && Config.LOCK_COMBAT_MODE.get()) {
            ((CombatModeAccessor) ctx.getSender()).setCombatMode(true);
            PacketHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new UUIDPacket(ctx.getSender().getUUID(), true));
        }
    }

    //Client
    public void handleToggleMode(NetworkEvent.Context ctx, UUID uuid, boolean bool) {
    }
}
