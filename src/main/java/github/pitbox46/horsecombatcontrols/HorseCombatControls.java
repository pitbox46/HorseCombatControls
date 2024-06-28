package github.pitbox46.horsecombatcontrols;

import com.mojang.serialization.Codec;
import github.pitbox46.horsecombatcontrols.network.ModClientPayloadHandler;
import github.pitbox46.horsecombatcontrols.network.CombatModePacket;
import github.pitbox46.horsecombatcontrols.network.ModServerPayloadHandler;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.attachment.AttachmentType;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(HorseCombatControls.MODID)
public class HorseCombatControls {
    public static final String MODID = "horsecombatcontrols";
    public static final Logger LOGGER = LogManager.getLogger();

    public HorseCombatControls(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        container.getEventBus().addListener(this::registerPackets);
        ATTACHMENT_TYPES.register(container.getEventBus());
    }

    public static boolean isCombatMode(Player player) {
        return Config.LOCK_COMBAT_MODE.get() || player.getData(COMBAT_MODE);
    }

    public static void setCombatMode(Player player, boolean flag) {
         player.setData(COMBAT_MODE, Config.LOCK_COMBAT_MODE.get() || flag);
    }

    public void registerPackets(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(MODID);
        registrar.playBidirectional(
                CombatModePacket.TYPE,
                CombatModePacket.CODEC,
                new DirectionalPayloadHandler<>(
                        ModClientPayloadHandler::handle,
                        ModServerPayloadHandler::handle
                )
        );
    }

    //region Registry
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

    public static final Supplier<AttachmentType<Boolean>> COMBAT_MODE = ATTACHMENT_TYPES.register(
            "combat_controls",
            () -> AttachmentType
                    .builder(() -> Config.LOCK_COMBAT_MODE.get())
                    .serialize(Codec.BOOL)
                    .build()
    );
    //endregion
}
