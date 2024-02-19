package github.pitbox46.horsecombatcontrols.mixins;

import github.pitbox46.horsecombatcontrols.Config;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixinServer extends LivingEntity {
    @Shadow
    protected float playerJumpPendingScale;
    @Shadow
    protected boolean allowStandSliding;
    @Unique
    private double horseCombatControls$prevSpeedPercent = 0F;

    protected AbstractHorseMixinServer(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Shadow
    public abstract boolean isStanding();

    @Inject(at = @At(value = "HEAD"), method = "getRiddenRotation", cancellable = true)
    private void replaceGetRiddenRotation(LivingEntity pEntity, CallbackInfoReturnable<Vec2> cir) {
        if (pEntity instanceof Player player && HorseCombatControls.isInCombatMode(player)) {
            float strafingMovement = pEntity.xxa * 0.5F;
            //Faster current speed -> slower rotation
            double deltaRotRaw = Math.atan(.05 / Math.abs(horseCombatControls$prevSpeedPercent)) * 180 / Math.PI;
            double deltaRot = Math.min(deltaRotRaw, 10); //Bound the speed at 10 degrees per tick
            if (Math.abs(strafingMovement) == 0) {
                deltaRot = 0;
            }

            cir.setReturnValue(new Vec2(pEntity.getXRot() * 0.5F, (float) (getYRot() + (deltaRot * (strafingMovement < 0 ? 1:-1)))));
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "getRiddenInput", cancellable = true)
    private void replaceGetRiddenInput(Player pPlayer, Vec3 pTravelVector, CallbackInfoReturnable<Vec3> cir) {
        if (HorseCombatControls.isInCombatMode(pPlayer)) {
            if (this.onGround() && this.playerJumpPendingScale==0.0F && this.isStanding() && !this.allowStandSliding) {
                cir.setReturnValue(Vec3.ZERO);
                return;
            }
            float forwardMovement = pPlayer.zza;

            double maxSpeedScale = 1;
            double maxSpeedScaleBack = 0.25;
            double acc = maxSpeedScale * 0.05;

            if (forwardMovement > 0 && horseCombatControls$prevSpeedPercent < maxSpeedScale) {
                horseCombatControls$prevSpeedPercent = Math.min(maxSpeedScale, horseCombatControls$prevSpeedPercent + acc);
            } else if (forwardMovement < 0 && horseCombatControls$prevSpeedPercent > -maxSpeedScaleBack) {
                horseCombatControls$prevSpeedPercent = Math.max(-maxSpeedScaleBack, horseCombatControls$prevSpeedPercent - acc);
            }

            if (Math.abs(horseCombatControls$prevSpeedPercent) < 0.05) {
                horseCombatControls$prevSpeedPercent *= 0.95;
            }

            cir.setReturnValue(new Vec3(0, 0, horseCombatControls$prevSpeedPercent));
        }
    }

    @Inject(at = @At(value = "HEAD"), method = "canPerformRearing", cancellable = true)
    private void cancelRandomRearing(CallbackInfoReturnable<Boolean> cir) {
        if (Config.CANCEL_RAND_REARING.get() && getControllingPassenger() instanceof Player && HorseCombatControls.isInCombatMode((Player) getControllingPassenger()))
            cir.setReturnValue(false);
    }
}
