package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.PlayerDuck;
import github.pitbox46.horsecombatcontrols.Config;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ModClientPayloadHandler {
    private static final Lazy<KeyMapping> TOGGLE_CONTROLS = Lazy.of(() -> new KeyMapping("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category"));

    private static final ModClientPayloadHandler INSTANCE = new ModClientPayloadHandler();

    public static ModClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(TOGGLE_CONTROLS.get());
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if(player == null || event.phase != TickEvent.Phase.END)
            return;
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
        PacketDistributor.SERVER.noArg().send(new CombatModePacket(flag));
    }

    //Handlers

    public void handle(CombatModePacket msg, PlayPayloadContext ctx) {
        if(Minecraft.getInstance().player == null)
            return;
        PlayerDuck accessor = (PlayerDuck) Minecraft.getInstance().player;
        if(!accessor.horseCombatControls$inCombatMode() && msg.combatMode()) {
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
        }
        accessor.horseCombatControls$setCombatMode(msg.combatMode());
    }
}
