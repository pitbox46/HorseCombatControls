package github.pitbox46.horsecombatcontrols;

import github.pitbox46.horsecombatcontrols.network.ClientProxy;
import github.pitbox46.horsecombatcontrols.network.CommonProxy;
import github.pitbox46.horsecombatcontrols.network.EmptyPacket;
import github.pitbox46.horsecombatcontrols.network.PacketHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("horsecombatcontrols")
public class HorseCombatControls {
    public static final Logger LOGGER = LogManager.getLogger();

    private static KeyMapping toggleControls;
    public static CommonProxy PROXY;
    private int tick = 0;

    public HorseCombatControls() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        toggleControls = new KeyMapping("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category");
        ClientRegistry.registerKeyBinding(toggleControls);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if(player != null && event.phase == TickEvent.Phase.END) {
            if(++tick > 20) {
                PacketHandler.CHANNEL.sendToServer(new EmptyPacket(EmptyPacket.Type.SYNC_MODE));
                tick = 0;
            }
            if (toggleControls.consumeClick()) {
                PacketHandler.CHANNEL.sendToServer(new EmptyPacket(EmptyPacket.Type.TOGGLE_MODE));
            }
        }
    }
}
