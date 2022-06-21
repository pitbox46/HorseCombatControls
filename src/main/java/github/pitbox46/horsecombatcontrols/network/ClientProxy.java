package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

public class ClientProxy extends CommonProxy {
    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void handleCombatModePacketClient(NetworkEvent.Context ctx, CombatModePacket msg) {
        if(Minecraft.getInstance().player != null)
            ((CombatModeAccessor) Minecraft.getInstance().player).setCombatMode(msg.combatMode());
    }
}
