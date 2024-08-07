package github.pitbox46.horsecombatcontrols.client.mixin;

import github.pitbox46.horsecombatcontrols.HCCConfigModel;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import github.pitbox46.horsecombatcontrols.client.HorseCombatControlsClient;
import net.minecraft.client.player.RemotePlayer;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseMixinClient extends LivingEntity {
    @Shadow
    protected float playerJumpPendingScale;
    @Shadow
    protected boolean allowStandSliding;
    @Unique
    private double horseCombatControls$prevSpeedPercent = 0F;

    protected AbstractHorseMixinClient(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Shadow
    public abstract boolean isStanding();

    @Inject(at = @At(value = "HEAD"), method = "getRiddenRotation", cancellable = true)
    private void replaceGetRiddenRotation(LivingEntity pEntity, CallbackInfoReturnable<Vec2> cir) {
        //Remote players should just use the rotation provided by the server
        if (pEntity instanceof RemotePlayer) {
            cir.setReturnValue(new Vec2(getXRot(), getYRot()));
            return;
        }
        if (pEntity instanceof Player player && HorseCombatControlsClient.combatMode) {
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
        if (HorseCombatControlsClient.combatMode) {
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

    @Inject(method = "getAmbientStandInterval", at = @At("HEAD"), cancellable = true)
    private void cancelRandomRearing(CallbackInfoReturnable<Integer> cir) {
        if (HorseCombatControls.CONFIG.cancelRandomRearing()) {
            cir.setReturnValue(Integer.MAX_VALUE);
        }
    }

    @Inject(method = "makeMad", at = @At("HEAD"), cancellable = true)
    private void cancelMakeRearing(CallbackInfo ci) {
        if (HorseCombatControls.CONFIG.cancelRandomRearing()) {
            ci.cancel();
        }
    }
}
