package github.pitbox46.horsecombatcontrols.mixins;

import github.pitbox46.horsecombatcontrols.CombatModeAccessor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public class PlayerMixin implements CombatModeAccessor {
    private boolean horseCombatMode;

    @Override
    public boolean inCombatMode() {
        return horseCombatMode;
    }

    @Override
    public boolean toggleCombatMode() {
        horseCombatMode = !horseCombatMode;
        return horseCombatMode;
    }

    @Override
    public void setCombatMode(boolean bool) {
        horseCombatMode = bool;
    }
}
