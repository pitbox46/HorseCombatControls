package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientProxy extends CommonProxy{
    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void handleControlModeReturn(NetworkEvent.Context ctx, ModeReturn msg) {
        HorseCombatControls.combatMode = msg.combatMode;
    }
}
