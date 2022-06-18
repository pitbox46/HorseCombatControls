package github.pitbox46.horsecombatcontrols.mixins;

import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity {
    public PlayerMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At(value = "HEAD"), method = "defineSynchedData")
    private void onDefineSynchedData(CallbackInfo ci) {
        this.entityData.define(HorseCombatControls.HORSE_COMBAT_MODE, false);
    }
}
