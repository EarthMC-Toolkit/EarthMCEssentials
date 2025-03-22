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

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.event.Level;

public class EventRegistry {
    static ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
    
    //private static final Identifier INFO_OVERLAY_LAYER = Identifier.of(EMCEssentials.MOD_ID, "info-overlay-layer");
    
    public static void RegisterCommands(EMCEssentials instance, CommandDispatcher<FabricClientCommandSource> dispatcher) {
        Set.of(
            // Overlay
            new NearbyCommand(instance),
            new TownlessCommand(instance),
            
            // Custom API
            new AllianceCommand(instance),
            new NewsCommand(instance),
            
            // Util
            new NetherCommand(),
            new RouteCommand()
        ).forEach(cmd -> cmd.registerSelf(dispatcher));
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

    @SuppressWarnings("deprecation")
    public static void RegisterHudEvents() {
        // We are using the new hud rendering method to put the overlay info before the vanilla chat layer,
        // though this is experimental and we could just go back to simple HudRenderCallback if need be.
        //
        // Update: I cannot get this to render :((
//        HudLayerRegistrationCallback.EVENT.register(layeredDrawer ->
//            layeredDrawer.attachLayerAfter(IdentifiedLayer., INFO_OVERLAY_LAYER, OverlayRenderer::RenderAllOverlays)
//        );
        
        HudRenderCallback.EVENT.register(OverlayRenderer::RenderAllOverlays);
    }
    
    public static void RegisterConnection() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            // Update the server name to the one we just joined.
            ModUtils.setServerName(ModUtils.currentServer());

            // Fires once every time we join with a slight delay to wait for OAPI update.
            exec.schedule(EventRegistry::OnJoin, 3, TimeUnit.SECONDS);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ModUtils.setServerName(null);
            EMCEssentials.instance().scheduler().reset();
            
            Messaging.sendDebugMessage("Disconnected. Stopped scheduler and reset server name.", Level.INFO);
        });
    }
    
    static void OnJoin() {
        //#region Detect which map client is on, if we are on EMC (or singleplayer if enabled).
        boolean onEMC = ModUtils.isConnectedToEMC();
        boolean singlePlayer =
            ModUtils.isInSinglePlayer() &&
            ModConfig.instance().general.enableInSingleplayer;
        
        if (!onEMC && !singlePlayer) return;
        Messaging.sendDebugMessage("New game session detected.", Level.INFO);
        //#endregion
        
        // Update overlay pos/state before we start rendering.
        ScreenInit.Refresh(); // TODO: Could be redundant if we do this in ScreenInit::before.
        
        RegisterRenderingEvents();
        TryInitScheduler(singlePlayer);
    }

    private static void RegisterRenderingEvents() {
        RegisterScreenEvents(); // Events for screen related things - like refreshing on config exit.
        RegisterHudEvents(); // Events for HUD rendering - like overlays that draw text to the screen.
    }
    
    private static void TryInitScheduler(boolean singlePlayer) {
        TaskScheduler scheduler = EMCEssentials.instance().scheduler();
        String clientMapName = singlePlayer ? "singleplayer" : getClientMap();
        
        // Don't display overlays if in queue, keep checking until client is in a map.
        if (clientMapName.equals("queue")) scheduler.reset();
        else scheduler.initMap();
    }
    
    private static String getClientMap() {
        for (KnownMap map : KnownMap.values()) {
            if (!EMCEssentials.instance().clientOnlineInSquaremap(map)) continue;
            return map.getName();
        }

        return "queue";
    }
}