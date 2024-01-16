package github.pitbox46.horsecombatcontrols;

import github.pitbox46.horsecombatcontrols.network.ModClientPayloadHandler;
import github.pitbox46.horsecombatcontrols.network.CombatModePacket;
import github.pitbox46.horsecombatcontrols.network.ModServerPayloadHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(HorseCombatControls.MODID)
public class HorseCombatControls {
    public static final String MODID = "horsecombatcontrols";
    public static final Logger LOGGER = LogManager.getLogger();

    public HorseCombatControls(IEventBus bus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLog);
        bus.addListener(this::registerPackets);
    }

    public static boolean isInCombatMode(Player player) {
        return ((PlayerDuck) player).horseCombatControls$inCombatMode();
    }

    public void registerPackets(final RegisterPayloadHandlerEvent event) {
        IPayloadRegistrar registrar = event.registrar(MODID);
        registrar.play(CombatModePacket.ID, CombatModePacket::new, handler -> handler
                .client((payload, context) -> {
                    context.workHandler().submitAsync(() -> {
                        ModClientPayloadHandler.getInstance().handle(payload, context);
                    }).exceptionally(e -> {
                        context.packetHandler().disconnect(Component.literal(e.getMessage()));
                        return null;
                    });
                })
                .server((payload, context) -> {
                    context.workHandler().submitAsync(() -> {
                        ModServerPayloadHandler.getInstance().handle(payload, context);
                    }).exceptionally(e -> {
                        context.packetHandler().disconnect(Component.literal(e.getMessage()));
                        return null;
                    });
                }));
    }

    @SubscribeEvent
    public void onPlayerLog(PlayerEvent.PlayerLoggedInEvent event) {
        if(!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ModServerPayloadHandler.setCombatModeServerVersion(player, isInCombatMode(player));
    }
}
