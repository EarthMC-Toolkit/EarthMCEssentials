package net.emc.emce.utils;

import com.google.gson.*;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.object.*;
import net.emc.emce.object.exception.APIException;
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

public class EarthMCAPI {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ModConfig config = ModConfig.instance();
    public static final Pattern urlSchemePattern = Pattern.compile("^[a-z][a-z0-9+\\-.]*://");
    public static APIData apiData;

    public static CompletableFuture<JsonArray> getTownless() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) JsonParser.parseString(getURL(getRoute(APIRoute.TOWNLESS)));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
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
                    if (!player.getEntityWorld().getDimension().isBedWorking())
                        return new JsonArray();

                    JsonArray array = (JsonArray) JsonParser.parseString(getURL(getRoute(APIRoute.NEARBY) +
                            (int) player.getX() + "/" +
                            (int) player.getZ() + "/" +
                            xBlocks + "/" + zBlocks));

                    for (int i = 0; i < array.size(); i++) {
                        JsonObject currentObj = (JsonObject) array.get(i);
                        if (currentObj.get("name").getAsString().equals(client.player.getName().asString()))
                            array.remove(i);
                    }
                    return array;
                } else
                    return EarthMCEssentials.instance().getNearbyPlayers();
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return EarthMCEssentials.instance().getNearbyPlayers();
            }
        });
    }

    public static CompletableFuture<Resident> getResident(String residentName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new Resident((JsonObject) JsonParser.parseString(getURL(getRoute(APIRoute.RESIDENTS) + residentName)));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new Resident(residentName);
            }
        });
    }

    public static CompletableFuture<JsonArray> getTowns() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) JsonParser.parseString(getURL(getRoute(APIRoute.TOWNS)));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }

    public static CompletableFuture<ServerData> getServerData() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new ServerData((JsonObject) JsonParser.parseString(getURL(getRoute(APIRoute.SERVER_INFO))));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new ServerData();
            }
        });
    }

    public static CompletableFuture<JsonArray> getNations() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) JsonParser.parseString(getURL(getRoute(APIRoute.NATIONS)));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }

    public static CompletableFuture<NewsData> getNews() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new NewsData((JsonObject) JsonParser.parseString(getURL(getRoute(APIRoute.NEWS))));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new NewsData();
            }
        });
    }

    public static CompletableFuture<APIData> API() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new APIData((JsonObject) JsonParser.parseString(
                    getURL("https://raw.githubusercontent.com/EarthMC-Stats/EarthMCEssentials" +
                           "/main/src/main/resources/api.json")));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new APIData();
            }
        });
    }

    public static String getRoute(APIRoute routeType) {
        API().thenAccept(data -> apiData = data);
        String route;

        switch(routeType) {
            case TOWNLESS -> route = apiData.routes.townless;
            case NATIONS -> route = apiData.routes.nations;
            case RESIDENTS -> route = apiData.routes.residents;
            case PLAYERS -> route = apiData.routes.players;
            case TOWNS -> route = apiData.routes.towns;
            case ALLIANCES -> route = apiData.routes.alliances;
            case NEARBY -> route = apiData.routes.nearby;
            case SERVER_INFO -> route = apiData.routes.serverInfo;
            case NEWS -> route = apiData.routes.news;
            default -> throw new IllegalStateException("Unexpected value: " + routeType);
        }

        MsgUtils.sendDebugMessage("GETTING ROUTE - " + apiData.getDomain() + route + "/");
        return apiData.getDomain() + route + "/";
    }

    private static String getURL(String urlString) throws APIException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            final HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (HttpTimeoutException e) {
                throw new APIException("API did not return any data after 5 seconds for URL '" + urlString + "'.");
            }

            if (response.statusCode() != 200)
                throw new APIException("API returned response code " + response.statusCode() + " for URL: " + urlString);

            return response.body();
        } catch (Exception e) {
            throw new APIException(e.getMessage());
        }
    }
}
