package net.emc.emce;

import com.google.gson.JsonArray;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.emc.emce.commands.*;
import net.emc.emce.config.ModConfig;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.object.Resident;
import net.emc.emce.object.ServerData;
import net.emc.emce.config.ConfigUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class EarthMCEssentials implements ModInitializer {

    private static final Logger logger = LogManager.getLogger(EarthMCEssentials.class);

    private static Resident client;
    private static ModConfig config;
    private static boolean shouldRender = false;
    private static boolean debugModeEnabled = false;

    private static JsonArray townlessResidents;
    private static JsonArray nearbyPlayers;
    private static JsonArray nations;
    private static JsonArray towns;
    private static ServerData serverData;

    KeyBinding configKeybinding;

    private static final String[] colors = new String[] { "BLUE", "DARK_BLUE", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "RED", "DARK_RED",
                                                    "LIGHT_PURPLE", "DARK_PURPLE", "YELLOW", "GOLD", "GRAY", "DARK_GRAY", "BLACK", "WHITE" };

    @Override
    public void onInitialize() {

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

        configKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("Open Config Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"));

        townlessResidents = new JsonArray();
        nearbyPlayers = new JsonArray();
        nations = new JsonArray();
        towns = new JsonArray();

        // Register client-sided commands.
        InfoCommands.registerNationInfoCommand();
        InfoCommands.registerTownInfoCommand();
        NearbyCommand.register();
        NetherCommand.register();
        QueueCommand.register();
        ToggleDebugCommand.register();
        TownlessCommand.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Pressed F4 (Config Menu)
            if (configKeybinding.wasPressed()) {
                Screen screen = ConfigUtils.getConfigBuilder().build();

                client.setScreen(screen);
		    }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> OverlayRenderer.render(matrixStack));
    }

    public static MinecraftClient getClient() {
        return MinecraftClient.getInstance();
    }

    public static Resident getClientResident() {
        return client;
    }

    public static void setClientResident(Resident clientResident) {
        client = clientResident;
    }

    public static ModConfig getConfig() {
        return config;
    }

    public static String[] getColors() {
        return colors;
    }

    public static boolean shouldRender() {
        return shouldRender;
    }

    public static boolean isDebugModeEnabled() {
        return debugModeEnabled;
    }

    public static JsonArray getTownless() {
        return townlessResidents;
    }

    public static JsonArray getNations() {
        return nations;
    }

    public static JsonArray getTowns() {
        return towns;
    }

    public static JsonArray getNearbyPlayers() {
        return nearbyPlayers;
    }

    public static void setDebugModeEnabled(boolean debugModeEnabled) {
        EarthMCEssentials.debugModeEnabled = debugModeEnabled;
    }

    public static void setShouldRender(boolean shouldRender) {
        EarthMCEssentials.shouldRender = shouldRender;
    }

    public static ServerData getServerData() {
        return serverData;
    }

    public static void setServerData(ServerData serverData) {
        EarthMCEssentials.serverData = serverData;
    }

    public static void setNations(JsonArray nations) {
        EarthMCEssentials.nations = nations;
    }

    public static void setNearbyPlayers(JsonArray nearbyPlayers) {
        EarthMCEssentials.nearbyPlayers = nearbyPlayers;
    }

    public static void setTowns(JsonArray towns) {
        EarthMCEssentials.towns = towns;
    }

    public static void setTownlessResidents(JsonArray townlessResidents) {
        EarthMCEssentials.townlessResidents = townlessResidents;
    }

    public static Logger getLogger() {
        return logger;
    }
}