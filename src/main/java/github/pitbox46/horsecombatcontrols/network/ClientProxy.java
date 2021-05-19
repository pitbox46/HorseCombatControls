package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;

public class ClientProxy extends CommonProxy {
    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void handleToggleMode(NetworkEvent.Context ctx, UUID uuid, boolean bool) {
        if(Minecraft.getInstance().world != null && Minecraft.getInstance().world.getPlayerByUuid(uuid) instanceof AbstractClientPlayerEntity)
            ((CombatModeAccessor) Minecraft.getInstance().world.getPlayerByUuid(uuid)).setCombatMode(bool);
    }
}
