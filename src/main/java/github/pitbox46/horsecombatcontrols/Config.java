package github.pitbox46.horsecombatcontrols;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final String CATEGORY_GENERAL = "general";

    public static ForgeConfigSpec SERVER_CONFIG;

    public static ForgeConfigSpec.BooleanValue LOCK_COMBAT_MODE;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

        SERVER_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        LOCK_COMBAT_MODE = SERVER_BUILDER.comment("Lock controls to the combat mode")
                .define("lock_combat_mode", false);

        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
}
