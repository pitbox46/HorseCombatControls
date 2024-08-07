package github.pitbox46.horsecombatcontrols.client;

import github.pitbox46.horsecombatcontrols.HCCConfigModel;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import github.pitbox46.horsecombatcontrols.network.CombatModePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class HorseCombatControlsClient implements ClientModInitializer {
    private static KeyMapping toggleControls;
    public static boolean combatMode = false;

    @Override
    public void onInitializeClient() {
        toggleControls = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.horsecombatcontrols.toggle",
                89,
                "key.horsecombatcontrols.category"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleControls.consumeClick()) {
                LocalPlayer player = client.player;
                if (player != null) {
                    if (HorseCombatControls.CONFIG.lockCombatMode()) {
                        player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);

                        setCombatModeClientVersion(player, true);
                    } else {
                        setCombatModeClientVersion(player, !combatMode);
                    }
                    if (player.tickCount % 100 == 0 && HorseCombatControls.CONFIG.lockCombatMode()) {
                        setCombatModeClientVersion(player, true);
                    }
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(CombatModePacket.TYPE, ((packet, context) -> {
            boolean flag = packet.combatMode();
            context.client().execute(() -> combatMode = flag);
        }));
    }

    public static void setCombatModeClientVersion(Player player, boolean flag) {
        combatMode = flag;
        ClientPlayNetworking.send(new CombatModePacket(combatMode));
    }
}