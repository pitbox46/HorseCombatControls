package github.pitbox46.horsecombatcontrols;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.BooleanValue LOCK_COMBAT_MODE;
    public static ForgeConfigSpec.BooleanValue CANCEL_RAND_REARING;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        LOCK_COMBAT_MODE = SERVER_BUILDER.comment("Lock controls to the combat mode")
                .define("lock_combat_mode", false);
        CANCEL_RAND_REARING = SERVER_BUILDER.comment("Cancel horse random rearing")
                .define("cancel_rand_rearing", true);

        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
