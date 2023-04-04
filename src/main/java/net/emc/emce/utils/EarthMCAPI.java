package net.emc.emce.utils;

import com.google.gson.JsonArray;
import io.github.emcw.core.EMCMap;
import io.github.emcw.entities.*;
import io.github.emcw.exceptions.APIException;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.utils.Request;
import net.emc.emce.config.ModConfig;
import net.emc.emce.objects.API.APIData;
import net.emc.emce.objects.API.APIRoute;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.emcw.utils.GsonUtil.serialize;
import static net.emc.emce.EarthMCEssentials.instance;

public class EarthMCAPI {
    private static final ModConfig config = ModConfig.instance();
    public static APIData apiData = new APIData();
    private static final String endpoints =
            "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMCEssentials/main/src/main/resources/api.json";

    private static EMCMap currentMap() {
        return Objects.equals(instance().mapName, "nova")
                ? instance().wrapper.getNova()
                : instance().wrapper.getAurora();
    }

    public static @Nullable Town getTown(String name) {
        try { return currentMap().Towns.single(name); }
        catch (MissingEntryException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static @Nullable Nation getNation(String name) {
        try { return currentMap().Nations.single(name); }
        catch (MissingEntryException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Map<String, Player> getTownless() {
        return currentMap().Players.townless();
    }

    public static Map<String, Player> onlinePlayers() { return currentMap().Players.online(); }

    @Nullable
    public static Player getOnlinePlayer(String playerName) {
        return currentMap().Players.getOnline(playerName);
    }

    public static boolean clientOnline(String map) {
        instance().mapName = map; // getOnlinePlayer uses mapName.

        System.out.println(serialize(onlinePlayers().values()));
        return onlinePlayers().containsKey(clientName());
    }

    @SuppressWarnings("unused")
    public static Map<String, Resident> getResidents() {
        return currentMap().Residents.all();
    }

    public static @Nullable Resident getResident(String name) {
        try { return currentMap().Residents.single(name); }
        catch (MissingEntryException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Map<String, Player> allPlayers() {
        return currentMap().Players.all();
    }

    public static @Nullable Player getPlayer(String name) {
        return allPlayers().getOrDefault(name, null);
    }

    @Contract(" -> new")
    public static @NotNull Map<String, Player> getNearby() {
        return getNearby(config.nearby.xBlocks, config.nearby.zBlocks);
    }

    @Contract("_, _ -> new")
    public static @NotNull Map<String, Player> getNearby(int xBlocks, int zBlocks) {
        Map<String, Player> result = new ConcurrentHashMap<>();

        try {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;

            if (player == null) return result;
            if (!player.getEntityWorld().getDimension().bedWorks()) return result;

            EMCMap curMap = currentMap();
            Integer x = (int) player.getX(),
                    y = (int) player.getY();

            Map<String, Player> nearby = curMap.Players.getNearby(allPlayers(), x, y, xBlocks, zBlocks);
            nearby.remove(clientName());

            return nearby;
        } catch (Exception e) {
            Messaging.sendDebugMessage("Error fetching nearby!", e);
            return result;
        }
    }

    @Contract(" -> new")
    public static @NotNull CompletableFuture<JsonArray> getAlliances() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Request.send(getRoute(APIRoute.ALLIANCES));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }

    @Contract(" -> new")
    public static @NotNull CompletableFuture<APIData> fetchAPI() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new APIData(Request.send(endpoints));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new APIData();
            }
        });
    }

    public static void fetchEndpoints() {
        Messaging.sendDebugMessage("Fetching endpoint URLs");
        fetchAPI().thenAccept(data -> apiData = data);
    }

    public static @NotNull String getRoute(@NotNull APIRoute routeType) {
        String route;

        switch(routeType) {
            case ALL_PLAYERS -> route = apiData.routes.allPlayers;
            case ALLIANCES -> route = apiData.routes.alliances;
            default -> throw new IllegalStateException("Unexpected value: " + routeType);
        }

        String endpoint = instance().mapName + route;
        Messaging.sendDebugMessage("Requesting endpoint -> " + endpoint);

        return apiData.getDomain() + endpoint;
    }

    @Nullable
    public static String clientName() {
        ClientPlayerEntity pl = MinecraftClient.getInstance().player;
        return pl == null ? null : pl.getName().getString();
    }
}