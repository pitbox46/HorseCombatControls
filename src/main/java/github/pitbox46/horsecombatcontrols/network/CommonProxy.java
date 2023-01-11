package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import github.pitbox46.horsecombatcontrols.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class CommonProxy {
    public CommonProxy() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        PacketHandler.init();
    }

    public static void setCombatModeServerVersion(ServerPlayer player, boolean flag) {
        if(((CombatModeAccessor) player).inCombatMode() == flag)
            return;
        ((CombatModeAccessor) player).setCombatMode(flag);
        PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new CombatModePacket(flag));
    }

    public void handleCombatModePacketServer(NetworkEvent.Context ctx, CombatModePacket msg) {
        if(ctx.getSender() != null)
            ((CombatModeAccessor) ctx.getSender()).setCombatMode(msg.combatMode());
    }

    public void handleCombatModePacketClient(NetworkEvent.Context context, CombatModePacket msg) {

    }
}
