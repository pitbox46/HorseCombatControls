package github.pitbox46.horsecombatcontrols.network;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonProxy {
    public final Map<UUID, Boolean> playerToggleMap = new HashMap<>();

    public CommonProxy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    public void handleToggleControls(NetworkEvent.Context ctx) {
        UUID uuid = null;
        if(ctx.getSender() != null) uuid = ctx.getSender().getUniqueID();
        if(playerToggleMap.containsKey(uuid)) playerToggleMap.put(uuid, !playerToggleMap.get(uuid));
        else playerToggleMap.put(uuid, true);
    }

    public void handleControlModeQuery(NetworkEvent.Context ctx) {
        if(ctx.getSender() != null && playerToggleMap.containsKey(ctx.getSender().getUniqueID()))
            PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(ctx::getSender), new ModeReturn(playerToggleMap.get(ctx.getSender().getUniqueID())));
    }

    //Client
    public void handleControlModeReturn(NetworkEvent.Context ctx, ModeReturn msg) {
    }
}
