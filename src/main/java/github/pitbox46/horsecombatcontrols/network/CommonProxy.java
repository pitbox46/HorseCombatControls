package github.pitbox46.horsecombatcontrols.network;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommonProxy {
    public static final Map<UUID, Boolean> PLAYER_TOGGLE_MAP = new HashMap<>();

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
        if(PLAYER_TOGGLE_MAP.containsKey(uuid)) PLAYER_TOGGLE_MAP.put(uuid, !PLAYER_TOGGLE_MAP.get(uuid));
        else PLAYER_TOGGLE_MAP.put(uuid, true);
    }
}
