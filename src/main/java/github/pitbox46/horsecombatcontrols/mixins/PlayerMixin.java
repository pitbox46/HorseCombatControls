package github.pitbox46.horsecombatcontrols.mixins;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import github.pitbox46.horsecombatcontrols.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements CombatModeAccessor {
    private boolean combatMode = false;

    public PlayerMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At(value = "HEAD"), method = "addAdditionalSaveData")
    private void onAddAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        pCompound.putBoolean("combatMode", inCombatMode());
    }

    @Inject(at = @At(value = "HEAD"), method = "readAdditionalSaveData")
    private void onReadAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        if(pCompound.contains("combatMode"))
            this.setCombatMode(pCompound.getBoolean("combatMode"));
        else
            this.setCombatMode(Config.LOCK_COMBAT_MODE.get());
    }

    @Override
    public boolean inCombatMode() {
        return Config.LOCK_COMBAT_MODE.get() || combatMode;
    }

    @Override
    public void setCombatMode(boolean flag) {
        this.combatMode = Config.LOCK_COMBAT_MODE.get() || flag;
    }
}
