package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import github.pitbox46.horsecombatcontrols.Config;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final RegisterKeyMappingsEvent event) {
        toggleControls = new KeyMapping("key.horsecombatcontrols.toggle", 89, "key.horsecombatcontrols.category");
        event.register(toggleControls);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Player player = Minecraft.getInstance().player;
        if(player == null || event.phase != TickEvent.Phase.END)
            return;
        if (toggleControls.consumeClick()) {
            if(Config.LOCK_COMBAT_MODE.get()) {
                setCombatModeClientVersion(player, true);
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
            }
            else
                setCombatModeClientVersion(player, !HorseCombatControls.isInCombatMode(player));
        }
        if(Config.LOCK_COMBAT_MODE.get() && player.level.getGameTime() % 200 == 104 && !HorseCombatControls.isInCombatMode(player)) {
            setCombatModeClientVersion(player, true);
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
        }
    }

    public static void setCombatModeClientVersion(Player player, boolean flag) {
        ((CombatModeAccessor) player).setCombatMode(flag);
        PacketHandler.CHANNEL.sendToServer(new CombatModePacket(flag));
    }

    //Handlers

    @Override
    public void handleCombatModePacketClient(NetworkEvent.Context ctx, CombatModePacket msg) {
        if(Minecraft.getInstance().player == null)
            return;
        CombatModeAccessor accessor = (CombatModeAccessor) Minecraft.getInstance().player;
        if(!accessor.inCombatMode() && msg.combatMode()) {
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("message.horsecombatcontrols.locked"), true);
        }
        accessor.setCombatMode(msg.combatMode());
    }
}
