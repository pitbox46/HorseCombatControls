package github.pitbox46.horsecombatcontrols.mixins;

import github.pitbox46.horsecombatcontrols.Config;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractHorse.class)
public abstract class ControlsMixin extends LivingEntity {
    @Shadow
    protected float playerJumpPendingScale;
    @Shadow
    private boolean allowStandSliding;
    private double previousZMotion = 0F;

    protected ControlsMixin(EntityType<? extends LivingEntity> type, Level worldIn) {
        super(type, worldIn);
    }

    @Shadow
    @Nullable
    public abstract Entity getControllingPassenger();

    @Shadow
    public abstract boolean isStanding();

    @Shadow
    public abstract double getCustomJump();

    @Shadow
    public abstract boolean isJumping();

    @Shadow
    public abstract void setIsJumping(boolean pJumping);

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;getControllingPassenger()Lnet/minecraft/world/entity/Entity;", ordinal = 0), method = "travel", cancellable = true)
    private void travelInject(Vec3 pTravelVector, CallbackInfo ci) {
        if (getControllingPassenger() instanceof Player && HorseCombatControls.isInCombatMode((Player) getControllingPassenger())) {
            Player passenger = (Player) this.getControllingPassenger();
            float strafingMovement = passenger.xxa * 0.5F;
            float forwardMovement = passenger.zza;

            boolean flag1 = (forwardMovement >= 0 && previousZMotion + forwardMovement * 0.01F < this.getAttributeValue(Attributes.MOVEMENT_SPEED));
            boolean flag2 = (forwardMovement <= 0 && previousZMotion + forwardMovement * 0.01F > -this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.25);
            if (flag1 || flag2)
                previousZMotion += forwardMovement * 0.01F;
            forwardMovement = previousZMotion > 0 ? 1 : -1;
            float movementSpeed = (float) Math.abs(previousZMotion);
            if (movementSpeed < .05)
                previousZMotion *= .95;

            double v = Math.atan(.01 / movementSpeed) * 180 / Math.PI;
            double rotationChange = (v < 10) ? v : 10;
            if (strafingMovement > 0)
                setYRot((float) (getYRot() - rotationChange));
            else if (strafingMovement < 0)
                setYRot((float) (getYRot() + rotationChange));

            this.yRotO = this.getYRot();
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;

            this.setXRot(passenger.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());

            if (this.onGround && this.playerJumpPendingScale == 0.0F && this.isStanding() && !this.allowStandSliding) {
                forwardMovement = 0.0F;
            }

            if (this.playerJumpPendingScale > 0.0F && !this.isJumping() && this.onGround) {
                double d0 = this.getCustomJump() * (double) this.playerJumpPendingScale * (double) this.getBlockJumpFactor();
                double d1;
                if (this.hasEffect(MobEffects.JUMP)) {
                    d1 = d0 + (double) ((float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1F);
                } else {
                    d1 = d0;
                }

                Vec3 vector3d = this.getDeltaMovement();
                this.setDeltaMovement(vector3d.x, d1, vector3d.z);
                this.setIsJumping(true);
                this.hasImpulse = true;
                net.minecraftforge.common.ForgeHooks.onLivingJump(this);
                if (previousZMotion > 0.0F) {
                    float f2 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
                    float f3 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
                    this.setDeltaMovement(this.getDeltaMovement().add(-0.4F * f2 * this.playerJumpPendingScale, 0.0D, 0.4F * f3 * this.playerJumpPendingScale));
                }

                this.playerJumpPendingScale = 0.0F;
            }

            this.flyingSpeed = this.getSpeed() * 0.1F;

            //Movement
            if (this.isControlledByLocalInstance()) {
                this.setSpeed(movementSpeed);
                super.travel(new Vec3(0, pTravelVector.y, forwardMovement));
            } else {
                this.setDeltaMovement(Vec3.ZERO);
            }

            if (this.onGround) {
                this.playerJumpPendingScale = 0.0F;
                this.setIsJumping(false);
            }

            this.calculateEntityAnimation(this, false);
            ci.cancel();
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/animal/horse/AbstractHorse;stand()V", ordinal = 0), method = "getAmbientSound", cancellable = true)
    private void cancelRandomRearing(CallbackInfoReturnable<SoundEvent> cir) {
        if (Config.CANCEL_RAND_REARING.get() && getControllingPassenger() instanceof Player && HorseCombatControls.isInCombatMode((Player) getControllingPassenger()))
            cir.setReturnValue(null);
    }
}
