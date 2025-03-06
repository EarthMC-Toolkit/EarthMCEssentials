package net.emc.emce;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import io.github.emcw.EMCWrapper;
import io.github.emcw.KnownMap;

import io.github.emcw.Squaremap;
import io.github.emcw.squaremap.entities.SquaremapResident;
import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;

import net.emc.emce.config.ModConfig;
import net.emc.emce.modules.EventRegistry;
import net.emc.emce.modules.OverlayRenderer;
import net.emc.emce.modules.TaskScheduler;
import net.emc.emce.utils.Messaging;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

@SuppressWarnings("LombokGetterMayBeUsed")
public class EMCEssentials implements ModInitializer {
    @Accessors(fluent = true)
    @Getter private static EMCEssentials instance;
    
    @Accessors(fluent = true)
    @Getter private static final Logger logger = LoggerFactory.getLogger(EMCEssentials.class);
    
    @Accessors(fluent = true)
    @Getter private final TaskScheduler scheduler = new TaskScheduler();
    
    public KnownMap currentMap = KnownMap.AURORA;
    public static EMCWrapper emcw = new EMCWrapper()
        .registerSquaremap(KnownMap.AURORA);

    @Getter @Setter private JsonObject clientPlayer = null; // From the OAPI
    @Setter private boolean shouldRender = false;

    private Set<String> townlessNames = new HashSet<>();
    private Map<String, SquaremapOnlinePlayer> nearbyPlayers = new ConcurrentHashMap<>();
    
    public static KeyBinding configKeybinding;
    private ModConfig config = null;
    private boolean debugModeEnabled = false;

    @Override
    public void onInitialize() {
        instance = this;

        AutoConfig.register(ModConfig.class, GsonConfigSerializer::new);
        initConfig();

        configKeybinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "Open Config Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F4, "EarthMC Essentials"
        ));

        EventRegistry.RegisterClientTick();
        EventRegistry.RegisterConnection(this);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            EventRegistry.RegisterCommands(this, dispatcher)
        );
    }

    public ModConfig config() { return config; }
    public void initConfig() { config = AutoConfig.getConfigHolder(ModConfig.class).getConfig(); }

    public boolean shouldRender() {
        if (!config.general.enableMod) return false;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;

        boolean f3DebugOpen = client.getDebugHud().shouldShowDebugHud();
        if (f3DebugOpen) return false;

        return shouldRender;
    }

    public void setDebugEnabled(boolean enabled) {
        this.debugModeEnabled = enabled;

        if (enabled) Messaging.sendPrefixed("msg_debug_enabled");
        else Messaging.sendPrefixed("msg_debug_disabled");
    }

    public boolean debugEnabled() {
        return this.debugModeEnabled;
    }

    //#region Nearby
    public Map<String, SquaremapOnlinePlayer> getNearbyPlayers() {
        return nearbyPlayers;
    }
    
    public void updateNearbyPlayers() {
        ModConfig config = ModConfig.instance();
        setNearbyPlayers(fetchNearbyPlayers(config.nearby.xBlocks, config.nearby.zBlocks));
    }
    
    public void setNearbyPlayers(Map<String, SquaremapOnlinePlayer> nearbyPlayers) {
        this.nearbyPlayers = nearbyPlayers;
        OverlayRenderer.UpdateStates(false, true);
    }
    
    public Map<String, SquaremapOnlinePlayer> fetchNearbyPlayers(int xBlocks, int zBlocks) {
        Map<String, SquaremapOnlinePlayer> result = new ConcurrentHashMap<>();
        
        try {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            
            // Check if client's player is valid, and we can sleep in a bed in this world.
            if (player == null) return result;
            if (!player.getEntityWorld().getDimension().bedWorks()) return result;
            
            int playerX = (int) player.getX();
            int playerZ = (int) player.getZ();

            result = getCurrentMap().Players.getNearby(playerX, playerZ, xBlocks, zBlocks);
            result.remove(clientName());
        } catch (Exception e) {
            Messaging.sendDebugMessage("Error fetching nearby!", e);
        }
        
        return result;
    }
    //#endregion
    
    //#region Townless
    public Set<String> getTownless() {
        return townlessNames;
    }
    
    public void updateTownless() {
        setTownless(fetchTownless());
    }
    
    public void setTownless(@NotNull Map<String, SquaremapOnlinePlayer> map) {
        // Make sure there is data to add.
        if (map.isEmpty()) return;

        townlessNames.clear();
        townlessNames = map.keySet();

        OverlayRenderer.SetTownless(townlessNames);
        OverlayRenderer.UpdateStates(true, false);
    }

    public Map<String, SquaremapOnlinePlayer> fetchTownless() {
        return getCurrentMap().Players.getByResidency(false);
    }
    //#endregion
    
    //#region Client stuff
    @Nullable
    public static String clientName() {
        ClientPlayerEntity pl = MinecraftClient.getInstance().player;
        return pl == null ? null : pl.getName().getString();
    }
    
    public boolean clientOnlineInSquaremap(KnownMap map) {
        Map<String, SquaremapOnlinePlayer> ops = emcw.getSquaremap(map).Players.getAll();
        return ops.containsKey(clientName());
    }
    //#endregion
    
    // This assumes squaremap will be used for all known maps.
    public Squaremap getCurrentMap() {
        return emcw.getSquaremap(currentMap);
    }
    
    @Nullable
    public static SquaremapResident squaremapPlayerToResident(Squaremap map, SquaremapOnlinePlayer op) {
        return map.Residents.getSingle(op.getName());
    }
}