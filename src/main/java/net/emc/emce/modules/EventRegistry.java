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
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;

import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;

public class EventRegistry {
    static ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    
    private static final Identifier INFO_OVERLAY_LAYER = Identifier.of(EMCEssentials.MOD_ID, "info-overlay-layer");
    
    public static void RegisterCommands(EMCEssentials instance, CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // Overlay
        new NearbyCommand(instance).registerSelf(dispatcher);
        new TownlessCommand(instance).registerSelf(dispatcher);
        
        // Custom API
        new AllianceCommand(instance).registerSelf(dispatcher);
        new NewsCommand(instance).registerSelf(dispatcher);
        
        // Util
        new NetherCommand().registerSelf(dispatcher);
    }

    public static void RegisterClientTick() {
        // Every tick, see if we are pressing F4.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (EMCEssentials.configKeybinding.wasPressed()) {
                TryOpenConfigScreen(client);
            }
        });
    }

    public static void TryOpenConfigScreen(MinecraftClient client) {
        if (!ModUtils.configOpen()) {
            try {
                Screen configScreen = AutoConfig.getConfigScreen(ModConfig.class, client.currentScreen).get();
                client.setScreen(configScreen);
            } catch (Exception e) {
                Messaging.sendDebugMessage("Error opening config screen.", e);
            }
        }
    }
    
    public static void RegisterScreenEvents() {
        ScreenEvents.BEFORE_INIT.register(ScreenInit::before);
        ScreenEvents.AFTER_INIT.register(ScreenInit::after);
    }

    public static void RegisterHudEvents() {
        // We are using the new hud rendering method to put the overlay info before the vanilla chat layer,
        // though this is experimental and we could just go back to simple HudRenderCallback if need be.
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer ->
            layeredDrawer.attachLayerBefore(IdentifiedLayer.CHAT, INFO_OVERLAY_LAYER, OverlayRenderer::RenderAllOverlays)
        );
    }
    
    public static void RegisterConnection(EMCEssentials instance) {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            // Update the server name to the one we just joined.
            ModUtils.setServerName(ModUtils.currentServer());

            // Allow some time for the OAPI to update. Fires once every time we join.
            exec.schedule(() -> {
                //#region Detect which map client is on, if we are on EMC.
                String clientMapName = getClientMap();
                if (clientMapName == null) return; // Not on EMC.
                
                System.out.println("EMCE > New game session detected.");
                Messaging.sendDebugMessage("New game session detected.");
                //#endregion

                //#region Run regardless of map
                ScreenInit.Refresh(); // First refresh (update overlay pos, offsets, shouldRender & debug)
                
                RegisterScreenEvents(); // Events for screen related things - like refreshing on config exit.
                RegisterHudEvents(); // Events for HUD rendering - like overlays that draw text to the screen.
                //#endregion

                if (!isMapQueue(clientMapName)) instance.scheduler().initMap();
                else instance.scheduler().reset();
            }, 3, TimeUnit.SECONDS);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            System.out.println("EMCE > Disconnected.");

            ModUtils.setServerName(null);
            instance.scheduler().reset();
        });
    }

    private static @Nullable String getClientMap() {
        if (!ModUtils.isConnectedToEMC()) return null;

        for (KnownMap map : KnownMap.values()) {
            if (!EMCEssentials.instance().clientOnlineInSquaremap(map)) continue;
            return map.getName();
        }

        return "queue";
    }

    private static boolean isMapQueue(String map) {
        return Objects.equals(map, "queue");
    }
}