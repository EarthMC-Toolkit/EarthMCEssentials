package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.emc.emce.config.ModConfig;
import net.emc.emce.objects.API.APIData;
import net.emc.emce.objects.API.APIRoute;
import net.emc.emce.objects.News.NewsData;
import net.emc.emce.objects.Resident;
import net.emc.emce.objects.exception.APIException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import static net.emc.emce.EarthMCEssentials.instance;

public class EarthMCAPI {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ModConfig config = ModConfig.instance();
    public static final Pattern urlSchemePattern = Pattern.compile("^[a-z][a-z0-9+\\-.]*://");

    public static APIData apiData = new APIData();

    public static JsonObject player = new JsonObject();

    public static CompletableFuture<JsonArray> getTownless() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) JsonParser.parseString(getURL(getRoute(APIRoute.TOWNLESS)));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }

    public static CompletableFuture<JsonArray> getNearby() {
        return getNearby(config.nearby.xBlocks, config.nearby.zBlocks);
    }

    public static CompletableFuture<JsonArray> getNearby(int xBlocks, int zBlocks) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MinecraftClient client = MinecraftClient.getInstance();
                ClientPlayerEntity player = client.player;

                if (player != null) {
                    if (!player.getEntityWorld().getDimension().bedWorks())
                        return new JsonArray();

                    JsonArray array = (JsonArray) JsonParser.parseString(getURL(getRoute(APIRoute.NEARBY) +
                            (int) player.getX() + "/" +
                            (int) player.getZ() + "/" +
                            xBlocks + "/" + zBlocks));

                    for (int i = 0; i < array.size(); i++) {
                        JsonObject currentObj = (JsonObject) array.get(i);
                        if (currentObj.get("name").getAsString().equals(client.player.getName().getContent()))
                            array.remove(i);
                    }
                    return array;
                } else
                    return instance().getNearbyPlayers();
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return instance().getNearbyPlayers();
            }
        });
    }

    public static CompletableFuture<Resident> getResident(String residentName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new Resident((JsonObject) JsonParser.parseString(getURL(getRoute(APIRoute.RESIDENTS) + residentName)));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new Resident(residentName);
            }
        });
    }

    public static CompletableFuture<JsonElement> getOnlinePlayer(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return JsonParser.parseString(getURL(getRoute(APIRoute.ONLINE_PLAYERS) + "/" + playerName));
            } catch (APIException e) {
                System.out.println(e.getMessage());
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new JsonObject();
            }
        });
    }

    public static CompletableFuture<JsonArray> getTowns() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) JsonParser.parseString(getURL(getRoute(APIRoute.TOWNS)));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }

    public static CompletableFuture<JsonArray> getNations() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) JsonParser.parseString(getURL(getRoute(APIRoute.NATIONS)));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }

    public static CompletableFuture<NewsData> getNews() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new NewsData((JsonObject) JsonParser.parseString(getURL(getRoute(APIRoute.NEWS))));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new NewsData(null);
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
        Messaging.sendDebugMessage("EMCE > Fetching endpoint URLs");

        fetchAPI().thenAccept(data -> {
            apiData = data;
            instance().scheduler().initMap();
        });
    }

    public static String getRoute(APIRoute routeType) {
        String route;

        switch(routeType) {
            case TOWNLESS -> route = apiData.routes.townless;
            case NATIONS -> route = apiData.routes.nations;
            case RESIDENTS -> route = apiData.routes.residents;
            case ALL_PLAYERS -> route = apiData.routes.allPlayers;
            case ONLINE_PLAYERS -> route = apiData.routes.onlinePlayers;
            case TOWNS -> route = apiData.routes.towns;
            case ALLIANCES -> route = apiData.routes.alliances;
            case NEARBY -> route = apiData.routes.nearby;
            case NEWS -> route = apiData.routes.news;
            default -> throw new IllegalStateException("Unexpected value: " + routeType);
        }

        String endpoint = instance().mapName + route;
        Messaging.sendDebugMessage("Requesting endpoint -> " + endpoint);

        return apiData.getDomain() + endpoint;
    }

    public static String clientName() {
        return MinecraftClient.getInstance().player.getName().getString();
    }

    public static boolean playerOnline(String map) {
        instance().mapName = map;

        JsonObject player = (JsonObject) getOnlinePlayer(clientName()).join();
        return player.has("name") && player.get("name").getAsString().equals(clientName());
    }

    private static String getURL(String urlString) throws APIException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .GET().build();

            final HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (HttpTimeoutException e) {
                throw new APIException("Request timed out after 5 seconds.\nEndpoint: " + urlString);
            }

            System.out.println(response.body());
            if (response.statusCode() != 200 && response.statusCode() != 304)
                throw new APIException("API Error! Response code: " + response.statusCode() + "\nEndpoint: " + urlString);

            return response.body();
        } catch (Exception e) { throw new APIException(e.getMessage()); }
    }
}