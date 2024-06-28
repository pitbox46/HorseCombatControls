package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.PlayerDuck;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ModServerPayloadHandler {
    public static void setCombatModeServerVersion(ServerPlayer player, boolean flag) {
        if(((PlayerDuck) player).horseCombatControls$inCombatMode() == flag) {
            return;
        }
        ((PlayerDuck) player).horseCombatControls$setCombatMode(flag);
        PacketDistributor.sendToPlayer(player, new CombatModePacket(flag));
    }

    public static void handle(CombatModePacket msg, IPayloadContext ctx) {
        Player player = ctx.player();
        ((PlayerDuck) player).horseCombatControls$setCombatMode(msg.combatMode());
    }
}
