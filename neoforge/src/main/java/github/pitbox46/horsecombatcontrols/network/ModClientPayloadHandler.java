package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.Config;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber(value = Dist.CLIENT)
public class ModClientPayloadHandler {
    private static final Lazy<KeyMapping> TOGGLE_CONTROLS = Lazy.of(() -> new KeyMapping("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category"));

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if(player == null) {
            return;
        }
        if (TOGGLE_CONTROLS.get().consumeClick()) {
            if(Config.LOCK_COMBAT_MODE.get()) {
                setAndSyncCombatMode(player, true);
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
            }
            else {
                setAndSyncCombatMode(player, !HorseCombatControls.isCombatMode(player));
            }
        }
        if(Config.LOCK_COMBAT_MODE.get() && player.level().getGameTime() % 200 == 104 && !HorseCombatControls.isCombatMode(player)) {
            setAndSyncCombatMode(player, true);
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
        }
    }

    public static void setAndSyncCombatMode(Player player, boolean flag) {
        HorseCombatControls.setCombatMode(player, flag);
        PacketDistributor.sendToServer(new CombatModePacket(flag));
    }

    @SubscribeEvent
    public static void onClientClone(ClientPlayerNetworkEvent.Clone event) {
        HorseCombatControls.setCombatMode(
                event.getNewPlayer(),
                HorseCombatControls.isCombatMode(event.getOldPlayer())
        );
    }

    //Handlers

    public static void handle(CombatModePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            if (!HorseCombatControls.isCombatMode(player) && Config.LOCK_COMBAT_MODE.get()) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
            }
            HorseCombatControls.setCombatMode(player, msg.combatMode());
        }).exceptionally(throwable -> {
            HorseCombatControls.LOGGER.catching(throwable);
            return null;
        });
    }

    @EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = HorseCombatControls.MODID)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_CONTROLS.get());
        }

        public static void onClientSetup(FMLClientSetupEvent clientSetupEvent, ModContainer container) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
    }
}
