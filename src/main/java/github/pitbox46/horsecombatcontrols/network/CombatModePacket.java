package github.pitbox46.horsecombatcontrols.network;

import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CombatModePacket(boolean combatMode) implements CustomPacketPayload {
    public static final Type<CombatModePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(HorseCombatControls.MODID, "combat_mode"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CombatModePacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            CombatModePacket::combatMode,
            CombatModePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
