package github.pitbox46.horsecombatcontrols;

import github.pitbox46.horsecombatcontrols.network.ClientProxy;
import github.pitbox46.horsecombatcontrols.network.CombatModePacket;
import github.pitbox46.horsecombatcontrols.network.CommonProxy;
import github.pitbox46.horsecombatcontrols.network.PacketHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("horsecombatcontrols")
public class HorseCombatControls {
    public static final Logger LOGGER = LogManager.getLogger();
    private static KeyMapping toggleControls;
    public static CommonProxy PROXY;
    private int tick = 0;

    public HorseCombatControls() {
        PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean isInCombatMode(Player player) {
        return ((CombatModeAccessor) player).inCombatMode();
    }

    public static void setCombatModeClientVersion(Player player, boolean flag) {
        ((CombatModeAccessor) player).setCombatMode(flag);
        PacketHandler.CHANNEL.sendToServer(new CombatModePacket(flag));
    }

    private void onClientSetup(final RegisterKeyMappingsEvent event) {
        toggleControls = new KeyMapping("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category");
        event.register(toggleControls);
    }

    @SubscribeEvent
    public void onPlayerLog(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) return;
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new CombatModePacket(isInCombatMode(player)));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && event.phase == TickEvent.Phase.END) {
            if (toggleControls.consumeClick()) {
                if(Config.LOCK_COMBAT_MODE.get()) {
                    player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
                    setCombatModeClientVersion(player, true);
                }
                else
                    setCombatModeClientVersion(player, !isInCombatMode(player));
            }
            if (tick++ > 100 && Config.LOCK_COMBAT_MODE.get()) {
                setCombatModeClientVersion(player, true);
                tick = 0;
            }
        }
    }
}
