package github.pitbox46.horsecombatcontrols;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Sync;

@io.wispforest.owo.config.annotation.Config(name = HorseCombatControls.MODID + "-config", wrapperName = "HCCConfig")
public class HCCConfigModel {
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean lockCombatMode = false;
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean cancelRandomRearing = true;
}
