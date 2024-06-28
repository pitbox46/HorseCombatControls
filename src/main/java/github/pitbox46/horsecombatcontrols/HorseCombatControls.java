package github.pitbox46.horsecombatcontrols;

import github.pitbox46.horsecombatcontrols.network.ModClientPayloadHandler;
import github.pitbox46.horsecombatcontrols.network.CombatModePacket;
import github.pitbox46.horsecombatcontrols.network.ModServerPayloadHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(HorseCombatControls.MODID)
public class HorseCombatControls {
    public static final String MODID = "horsecombatcontrols";
    public static final Logger LOGGER = LogManager.getLogger();

    public HorseCombatControls(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLog);
        container.getEventBus().addListener(this::registerPackets);
    }

    public static boolean isInCombatMode(Player player) {
        return ((PlayerDuck) player).horseCombatControls$inCombatMode();
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

    @SubscribeEvent
    public void onPlayerLog(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ModServerPayloadHandler.setCombatModeServerVersion(player, isInCombatMode(player));
    }
}
