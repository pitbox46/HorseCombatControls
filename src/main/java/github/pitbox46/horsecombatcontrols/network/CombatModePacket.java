package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CombatModePacket(boolean combatMode) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(HorseCombatControls.MODID, "combat_mode");

    public CombatModePacket(FriendlyByteBuf pBuffer) {
        this(pBuffer.readBoolean());
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBoolean(combatMode);
    }

    @Override
    public @NotNull ResourceLocation id() {
        return ID;
    }
}
