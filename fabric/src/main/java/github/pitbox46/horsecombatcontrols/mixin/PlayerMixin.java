package github.pitbox46.horsecombatcontrols.mixin;

import github.pitbox46.horsecombatcontrols.HCCConfigModel;
import github.pitbox46.horsecombatcontrols.HorseCombatControls;
import github.pitbox46.horsecombatcontrols.PlayerDuck;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements PlayerDuck {
    @Unique
    private boolean horseCombatControls$combatMode = false;

    public PlayerMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At(value = "HEAD"), method = "addAdditionalSaveData")
    private void onAddAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        pCompound.putBoolean("combatMode", horseCombatControls$inCombatMode());
    }

    @Inject(at = @At(value = "HEAD"), method = "readAdditionalSaveData")
    private void onReadAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
        if(pCompound.contains("combatMode"))
            this.horseCombatControls$setCombatMode(pCompound.getBoolean("combatMode"));
        else
            this.horseCombatControls$setCombatMode(HorseCombatControls.CONFIG.lockCombatMode());
    }

    @Override
    public boolean horseCombatControls$inCombatMode() {
        return HorseCombatControls.CONFIG.lockCombatMode() || horseCombatControls$combatMode;
    }

    @Override
    public void horseCombatControls$setCombatMode(boolean flag) {
        this.horseCombatControls$combatMode = HorseCombatControls.CONFIG.lockCombatMode() || flag;
    }
}
