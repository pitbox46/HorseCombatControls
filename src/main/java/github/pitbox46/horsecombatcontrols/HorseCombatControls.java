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

@OnlyIn(Dist.CLIENT)
@Mod("horsecombatcontrols")
public class HorseCombatControls {
    public static final Logger LOGGER = LogManager.getLogger();
    private static KeyBinding toggleControls;
    private static boolean combatMode;


    public HorseCombatControls() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        toggleControls = new KeyBinding("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category");
        ClientRegistry.registerKeyBinding(toggleControls);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if(toggleControls.isPressed() && Minecraft.getInstance().player != null) {
            combatMode = !combatMode;
        }
    }

    public static boolean inCombatMode() {
        return combatMode;
    }
}
