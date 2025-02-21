package net.emc.emce.modules;

import com.mojang.brigadier.CommandDispatcher;
import io.github.emcw.KnownMap;
import me.shedaniel.autoconfig.AutoConfig;
import net.emc.emce.EMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.events.commands.*;
import net.emc.emce.events.screen.ScreenInit;
import net.emc.emce.utils.Messaging;
import net.emc.emce.utils.ModUtils;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static net.emc.emce.utils.ModUtils.isConnectedToEMC;
import static net.emc.emce.utils.ModUtils.updateServerName;

public class EventRegistry {
    public static void RegisterCommands(EMCEssentials instance, CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // Register client-sided commands.
        //new InfoCommands(instance).register(dispatcher);
        new NearbyCommand(instance).register(dispatcher);
        new TownlessCommand(instance).register(dispatcher);
        new AllianceCommand(instance).register(dispatcher);
        new NetherCommand().register(dispatcher);
    }

    public static void RegisterClientTick() {
        // Every tick, see if we are pressing F4.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (EMCEssentials.configKeybinding.wasPressed()) {
                if (!ModUtils.configOpen()) {
                    try {
                        Screen configScreen = AutoConfig.getConfigScreen(ModConfig.class, client.currentScreen).get();
                        client.setScreen(configScreen);
                    } catch (Exception e) {
                        Messaging.sendDebugMessage("Error opening config screen.", e);
                    }
                }
            }
        });
    }

    public static void RegisterScreen() {
        ScreenEvents.BEFORE_INIT.register(ScreenInit::before);
        ScreenEvents.AFTER_INIT.register(ScreenInit::after);
    }

    public static void RegisterHud() {
        // TODO: HudRenderCallback was deprecated in favour of events.
        // We can maybe use HudLayerRegistrationCallback instead - not exactly sure how yet.
        //
        // Relevent: https://github.com/FabricMC/fabric/pull/4119
        HudRenderCallback.EVENT.register((drawCtx, renderTickCounter) -> {
            // Renders every overlay (townless, nearby etc) if allowed.
            OverlayRenderer.RenderAllOverlays(drawCtx);
        });
    }

    static ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    public static void RegisterConnection(EMCEssentials instance) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            updateServerName();

            // Allow 3 seconds for Dynmap to update.
            exec.schedule(() -> {
                //region Detect client map (if on emc)
                String curMap = getClientMap();
                if (curMap == null) return; // Don't do anything if not on EMC.

                System.out.println("EMCE > New game session detected.");
                //endregion

                //region Run regardless of map
                instance.setShouldRender(instance.config().general.enableMod);
                instance.setDebugEnabled(instance.config().general.debugLog);

                //fetchEndpoints();
                OverlayRenderer.Init();

                RegisterScreen();
                RegisterHud();
                //endregion

                if (!isMapQueue(curMap)) instance.scheduler().initMap();
                else instance.scheduler().reset();
            }, 3, TimeUnit.SECONDS);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            System.out.println("EMCE > Disconnected.");

            ModUtils.setServerName("");
            EMCEssentials.instance().scheduler().reset();
        });
    }

    private static @Nullable String getClientMap() {
        if (!isConnectedToEMC()) return null;

        if (EMCEssentials.clientOnlineInMap(KnownMap.AURORA)) {
            return KnownMap.AURORA.getName();
        }
        //if (clientOnline("nova")) return "nova";

        return "queue";
    }

    private static boolean isMapQueue(String map) {
        return Objects.equals(map, "queue");
    }
}