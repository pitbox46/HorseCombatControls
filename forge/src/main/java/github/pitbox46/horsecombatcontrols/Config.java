package github.pitbox46.horsecombatcontrols;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static ModConfigSpec SERVER_CONFIG;

    public static ModConfigSpec.BooleanValue LOCK_COMBAT_MODE;
    public static ModConfigSpec.BooleanValue CANCEL_RAND_REARING;
    public static ModConfigSpec.DoubleValue ROT_MULTI;

    static {
        ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();

        SERVER_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        LOCK_COMBAT_MODE = SERVER_BUILDER.comment("Lock controls to the combat mode")
                .define("lock_combat_mode", false);
        CANCEL_RAND_REARING = SERVER_BUILDER.comment("Cancel horse random and hurt rearing")
                .define("cancel_rand_rearing", true);
        ROT_MULTI = SERVER_BUILDER.comment("A multiplier for the rotation speed")
                .defineInRange("rot_mutli", 1D, 0, 100);

        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
