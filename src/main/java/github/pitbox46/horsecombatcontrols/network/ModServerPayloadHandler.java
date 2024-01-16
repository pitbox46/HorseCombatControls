package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.PlayerDuck;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ModServerPayloadHandler {
    private static final ModServerPayloadHandler INSTANCE = new ModServerPayloadHandler();

    public static ModServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public static void setCombatModeServerVersion(ServerPlayer player, boolean flag) {
        if(((PlayerDuck) player).horseCombatControls$inCombatMode() == flag) {
            return;
        }
        ((PlayerDuck) player).horseCombatControls$setCombatMode(flag);
        PacketDistributor.PLAYER.with(player).send(new CombatModePacket(flag));
    }

    public void handle(CombatModePacket msg, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ((PlayerDuck) player).horseCombatControls$setCombatMode(msg.combatMode()));
    }
}
