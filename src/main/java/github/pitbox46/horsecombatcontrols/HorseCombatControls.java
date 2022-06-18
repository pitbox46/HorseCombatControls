package github.pitbox46.horsecombatcontrols;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("horsecombatcontrols")
public class HorseCombatControls {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final EntityDataAccessor<Boolean> HORSE_COMBAT_MODE = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BOOLEAN);
    private static KeyMapping toggleControls;
    private int tick = 0;

    public HorseCombatControls() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static boolean isInCombatMode(Player player) {
        return player.getEntityData().get(HORSE_COMBAT_MODE);
    }

    public static void setCombatMode(Player player, boolean flag) {
        if (!flag && Config.LOCK_COMBAT_MODE.get()) {
            player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
            player.getEntityData().set(HORSE_COMBAT_MODE, true);
        } else
            player.getEntityData().set(HORSE_COMBAT_MODE, flag);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        toggleControls = new KeyMapping("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category");
        ClientRegistry.registerKeyBinding(toggleControls);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player != null && event.phase == TickEvent.Phase.END) {
            if (toggleControls.consumeClick()) {
                setCombatMode(player, !isInCombatMode(player));
            }
            if (tick++ > 100 && Config.LOCK_COMBAT_MODE.get()) {
                setCombatMode(player, true);
                tick = 0;
            }
        }
    }
}
