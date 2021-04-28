package github.pitbox46.horsecombatcontrols;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("horsecombatcontrols")
public class HorseCombatControls {
    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean combatMode;
    @OnlyIn(Dist.CLIENT)
    private static final KeyBinding toggleControls = new KeyBinding("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category");

    public HorseCombatControls() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(toggleControls);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(toggleControls.isPressed() && Minecraft.getInstance().player != null) {
            combatMode = !combatMode;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean inCombatMode() {
        return combatMode;
    }
}
