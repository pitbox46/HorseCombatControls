package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@EventBusSubscriber
public class ModServerPayloadHandler {
    public static void setAndSyncCombatMode(ServerPlayer player, boolean flag) {
        HorseCombatControls.setCombatMode(player, flag);
        PacketDistributor.sendToPlayer(player, new CombatModePacket(flag));
    }

    @SubscribeEvent
    public static void onPlayerLog(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ModServerPayloadHandler.setAndSyncCombatMode(player, HorseCombatControls.isCombatMode(player));
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            setAndSyncCombatMode(player, HorseCombatControls.isCombatMode(event.getOriginal()));
        }
    }

    public static void handle(CombatModePacket msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            HorseCombatControls.setCombatMode(player, msg.combatMode());
        }).exceptionally(throwable -> {
            HorseCombatControls.LOGGER.catching(throwable);
            return null;
        });
    }
}
