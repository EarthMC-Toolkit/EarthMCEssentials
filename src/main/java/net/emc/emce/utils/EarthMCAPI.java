package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.emcw.core.EMCMap;
import io.github.emcw.core.EMCWrapper;
import io.github.emcw.entities.*;
import io.github.emcw.exceptions.MissingEntryException;
import net.emc.emce.config.ModConfig;
import net.emc.emce.objects.API.APIData;
import net.emc.emce.objects.API.APIRoute;
import net.emc.emce.objects.exception.APIException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static net.emc.emce.EarthMCEssentials.instance;

public class EarthMCAPI {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ModConfig config = ModConfig.instance();

    public static APIData apiData = new APIData();

    public static JsonObject player = new JsonObject();
    public static final EMCWrapper wrapper = new EMCWrapper(true, true);

    private static EMCMap currentMap() {
        return Objects.equals(instance().mapName, "aurora") ? wrapper.getAurora() : wrapper.getNova();
    }

    public static @Nullable Town getTown(String name) {
        try { return currentMap().Towns.single(name); }
        catch (MissingEntryException e) { return null; }
    }

    public static @Nullable Nation getNation(String name) {
        try { return currentMap().Nations.single(name); }
        catch (MissingEntryException e) { return null; }
    }

    public static Map<String, Player> getTownless() {
        return currentMap().Players.townless();
    }

    public static Map<String, Player> onlinePlayers() { return currentMap().Players.online(); }

    public static Map<String, Resident> getResidents() {
        return currentMap().Residents.all();
    }

    public static @Nullable Resident getResident(String name) {
        try { return currentMap().Residents.single(name); }
        catch (MissingEntryException e) { return null; }
    }

    @Contract(" -> new")
    public static @NotNull CompletableFuture<Map<String, Player>> getNearby() {
        return getNearby(config.nearby.xBlocks, config.nearby.zBlocks);
    }

    @Contract("_, _ -> new")
    public static @NotNull CompletableFuture<Map<String, Player>> getNearby(int xBlocks, int zBlocks) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Player> result = Map.of();

            try {
                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player == null) return result;
                if (!player.getEntityWorld().getDimension().bedWorks()) return result;

                EMCMap curMap = currentMap();
                Integer x = (int) player.getX(),
                        y = (int) player.getY();

                Map<String, Player> nearby = curMap.Players.getNearby(curMap.Players.all(), x, y, xBlocks, zBlocks);
                nearby.remove(clientName());

                return nearby;
            } catch (Exception e) {
                Messaging.sendDebugMessage("Error fetching nearby!", e);
                return result;
            }
        });
    }

    public static CompletableFuture<JsonArray> getAlliances() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) JsonParser.parseString(getURL(getRoute(APIRoute.ALLIANCES)));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }

    public static CompletableFuture<APIData> fetchAPI() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new APIData((JsonObject) JsonParser.parseString(
                    getURL("https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMCEssentials" +
                           "/main/src/main/resources/api.json")));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new APIData();
            }
        });
    }

    public static void fetchEndpoints() {
        Messaging.sendDebugMessage("Fetching endpoint URLs");

        fetchAPI().thenAccept(data -> {
            apiData = data;

            // Out of queue, begin map check.
            if (instance().sessionCounter > 1)
                instance().scheduler().initMap();
        });
    }

    public static String getRoute(APIRoute routeType) {
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

    public static boolean playerOnline(String map) {
        instance().mapName = map; // getOnlinePlayer uses mapName.
        return onlinePlayers().containsKey(clientName());
    }

    private static String getURL(String urlString) throws APIException {
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(urlString)).header("Accept", "application/json");

            Duration timeout = Duration.ofSeconds(5);
            final HttpRequest req = builder.timeout(timeout).GET().build();
            final HttpResponse<String> response;

            try { response = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)); }
            catch (HttpTimeoutException e) { throw new APIException("Request timed out after 5 seconds.\nEndpoint: " + urlString); }

            List<Integer> codes = List.of(new Integer[]{ 200, 203, 304 });
            if (!codes.contains(response.statusCode()))
                throw new APIException("API Error! Response code: " + response.statusCode() + "\nEndpoint: " + urlString);

            return response.body();
        } catch (Exception e) { throw new APIException(e.getMessage()); }
    }
}