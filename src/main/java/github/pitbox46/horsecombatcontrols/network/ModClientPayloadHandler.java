package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.PlayerDuck;
import github.pitbox46.horsecombatcontrols.Config;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT)
public class ModClientPayloadHandler {
    private static final Lazy<KeyMapping> TOGGLE_CONTROLS = Lazy.of(() -> new KeyMapping("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category"));

    private static final ModClientPayloadHandler INSTANCE = new ModClientPayloadHandler();

    public static ModClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Player player = Minecraft.getInstance().player;
        if(player == null) {
            return;
        }
        if (TOGGLE_CONTROLS.get().consumeClick()) {
            if(Config.LOCK_COMBAT_MODE.get()) {
                setCombatModeClientVersion(player, true);
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
            }
            else {
                setCombatModeClientVersion(player, !HorseCombatControls.isInCombatMode(player));
            }
        }
        if(Config.LOCK_COMBAT_MODE.get() && player.level().getGameTime() % 200 == 104 && !HorseCombatControls.isInCombatMode(player)) {
            setCombatModeClientVersion(player, true);
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
        }
    }

    public static void setCombatModeClientVersion(Player player, boolean flag) {
        ((PlayerDuck) player).horseCombatControls$setCombatMode(flag);
        PacketDistributor.sendToServer(new CombatModePacket(flag));
    }

    //Handlers

    public static void handle(CombatModePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null) {
                return;
            }
            PlayerDuck accessor = (PlayerDuck) Minecraft.getInstance().player;
            if (!accessor.horseCombatControls$inCombatMode() && msg.combatMode()) {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
            }
            accessor.horseCombatControls$setCombatMode(msg.combatMode());
        }).exceptionally(throwable -> {
            HorseCombatControls.LOGGER.catching(throwable);
            return null;
        });
    }

    @EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = HorseCombatControls.MODID)
    static class ModEvents {
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            event.register(TOGGLE_CONTROLS.get());
        }
    }
}
