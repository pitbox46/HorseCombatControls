package github.pitbox46.horsecombatcontrols;

import github.pitbox46.horsecombatcontrols.HCCConfig;
import github.pitbox46.horsecombatcontrols.network.CombatModePacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HorseCombatControls implements ModInitializer {
    public static final String MODID = "horsecombatcontrols";
    public static final Logger LOGGER = LogManager.getLogger();
    public static HCCConfig CONFIG = HCCConfig.createAndLoad();

    @Override
    public void onInitialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(isInCombatMode(handler.getPlayer()));
            ServerPlayNetworking.send(handler.getPlayer(), CombatModePacket.ID, buf);
        });

        ServerPlayNetworking.registerGlobalReceiver(CombatModePacket.ID, ((server, player, handler, buf, responseSender) -> {
            boolean flag = buf.readBoolean();
            server.execute(() -> ((PlayerDuck) player).horseCombatControls$setCombatMode(flag));
        }));
    }

    public static boolean isInCombatMode(Player player) {
        return ((PlayerDuck) player).horseCombatControls$inCombatMode();
    }
}
