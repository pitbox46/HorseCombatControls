package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.Config;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import github.pitbox46.horsecombatcontrols.PlayerDuck;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;

public class ClientProxy extends CommonProxy {
    private static KeyMapping toggleControls;

    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Events::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(Events::onClientTick);
    }

    @Override
    public void handleCombatModePacketClient(NetworkEvent.Context ctx, CombatModePacket msg) {
        if(Minecraft.getInstance().player != null)
            ((PlayerDuck) Minecraft.getInstance().player).horseCombatControls$setCombatMode(msg.combatMode());
    }

    @OnlyIn(Dist.CLIENT)
    static class Events {
        private static void onClientSetup(final RegisterKeyMappingsEvent event) {
            toggleControls = new KeyMapping("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category");
            event.register(toggleControls);
        }

        private static void onClientTick(TickEvent.ClientTickEvent event) {
            Player player = Minecraft.getInstance().player;
            if (player != null && event.phase == TickEvent.Phase.END) {
                if (toggleControls.consumeClick()) {
                    if(Config.LOCK_COMBAT_MODE.get()) {
                        player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
                        HorseCombatControls.setCombatModeClientVersion(player, true);
                    }
                    else
                        HorseCombatControls.setCombatModeClientVersion(player, !HorseCombatControls.isInCombatMode(player));
                }
                if (player.tickCount % 100 == 0 && Config.LOCK_COMBAT_MODE.get()) {
                    HorseCombatControls.setCombatModeClientVersion(player, true);
                }
            }
        }
    }
}