package github.pitbox46.horsecombatcontrols.mixins;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractHorseEntity.class)
public abstract class ControlsMixin extends LivingEntity {
    protected ControlsMixin(EntityType<? extends LivingEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Shadow protected float jumpPower;
    @Shadow private boolean allowStandSliding;

    @Shadow @Nullable public abstract Entity getControllingPassenger();
    @Shadow public abstract boolean isRearing();
    @Shadow public abstract boolean isHorseJumping();
    @Shadow public abstract double getHorseJumpStrength();
    @Shadow public abstract void setHorseJumping(boolean jumping);

    private double previousZMotion = 0F;

    @Inject(at=@At(value = "INVOKE", target = "net/minecraft/entity/passive/horse/AbstractHorseEntity.getControllingPassenger()Lnet/minecraft/entity/Entity;", ordinal=0), method="travel(Lnet/minecraft/util/math/vector/Vector3d;)V", cancellable = true)
    private void travelInject(Vector3d travelVector, CallbackInfo ci) {
        if(getControllingPassenger() instanceof PlayerEntity && ((CombatModeAccessor) getControllingPassenger()).inCombatMode()) {
            LivingEntity livingentity = (LivingEntity)this.getControllingPassenger();
            float strafingMovement = livingentity.moveStrafing * 0.5F;
            float forwardMovement = livingentity.moveForward;

            if(     (forwardMovement >= 0 && previousZMotion + forwardMovement * 0.01F < this.getAttributeValue(Attributes.MOVEMENT_SPEED)) ||
                    (forwardMovement <= 0 && previousZMotion + forwardMovement * 0.01F > -this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.25))
                previousZMotion += forwardMovement * 0.01F;
            forwardMovement = previousZMotion > 0 ? 1 : -1;
            float movementSpeed = (float) Math.abs(previousZMotion);
            if(movementSpeed < .05) previousZMotion *= .95;

            double v = Math.atan(.01 / movementSpeed) * 180 / Math.PI;
            double rotationChange = (v < 10) ? v : 10;
            if(strafingMovement > 0)
                this.rotationYaw -= rotationChange;
            else if(strafingMovement < 0)
                this.rotationYaw += rotationChange;

            this.prevRotationYaw = this.rotationYaw;
            this.renderYawOffset = this.rotationYaw;
            this.rotationYawHead = this.renderYawOffset;

            this.rotationPitch = livingentity.rotationPitch * 0.5F;
            this.setRotation(this.rotationYaw, this.rotationPitch);

            if (this.onGround && this.jumpPower == 0.0F && this.isRearing() && !this.allowStandSliding) {
                forwardMovement = 0.0F;
            }

            if (this.jumpPower > 0.0F && !this.isHorseJumping() && this.onGround) {
                double d0 = this.getHorseJumpStrength() * (double)this.jumpPower * (double)this.getJumpFactor();
                double d1;
                if (this.isPotionActive(Effects.JUMP_BOOST)) {
                    d1 = d0 + (double)((float)(this.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
                } else {
                    d1 = d0;
                }

                Vector3d vector3d = this.getMotion();
                this.setMotion(vector3d.x, d1, vector3d.z);
                this.setHorseJumping(true);
                this.isAirBorne = true;
                net.minecraftforge.common.ForgeHooks.onLivingJump(this);
                if (previousZMotion > 0.0F) {
                    float f2 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F));
                    float f3 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F));
                    this.setMotion(this.getMotion().add((double)(-0.4F * f2 * this.jumpPower), 0.0D, (double)(0.4F * f3 * this.jumpPower)));
                }

                this.jumpPower = 0.0F;
            }

            this.jumpMovementFactor = this.getAIMoveSpeed() * 0.1F;

            //Movement
            if (this.canPassengerSteer()) {
                this.setAIMoveSpeed(movementSpeed);
                super.travel(new Vector3d(0, travelVector.y, forwardMovement));
            } else if (livingentity instanceof PlayerEntity) {
                this.setMotion(Vector3d.ZERO);
            }

            if (this.onGround) {
                this.jumpPower = 0.0F;
                this.setHorseJumping(false);
            }

            this.func_233629_a_(this, false);
            ci.cancel();
        }
    }

    @Inject(at=@At(value="INVOKE",target="net/minecraft/entity/passive/horse/AbstractHorseEntity.makeHorseRear()V", ordinal=0), method="getAmbientSound()Lnet/minecraft/util/SoundEvent;", cancellable=true)
    private void cancelRandomRearing(CallbackInfoReturnable<SoundEvent> cir) {
        if(getControllingPassenger() instanceof PlayerEntity && ((CombatModeAccessor) getControllingPassenger()).inCombatMode())
            cir.cancel();
    }
}
