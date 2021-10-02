package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;

public class ClientProxy extends CommonProxy {
    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void handleToggleMode(NetworkEvent.Context ctx, UUID uuid, boolean bool) {
        if(Minecraft.getInstance().level != null && Minecraft.getInstance().level.getPlayerByUUID(uuid) instanceof AbstractClientPlayer)
            ((CombatModeAccessor) Minecraft.getInstance().level.getPlayerByUUID(uuid)).setCombatMode(bool);
    }
}
