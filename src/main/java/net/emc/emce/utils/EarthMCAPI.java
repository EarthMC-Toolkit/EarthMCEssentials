package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.object.APIData;
import net.emc.emce.object.APIRoute;
import net.emc.emce.object.NewsData;
import net.emc.emce.object.Resident;
import net.emc.emce.object.ServerData;
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

public class EarthMCAPI {
    private static final HttpClient client = HttpClient.newHttpClient();
    public static APIData apiData;

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
        return getNearby(EarthMCEssentials.instance().getConfig().nearby.xBlocks, EarthMCEssentials.instance().getConfig().nearby.zBlocks);
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
                Messaging.sendDebugMessage(e.getMessage(), e);
                return EarthMCEssentials.instance().getNearbyPlayers();
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

    public static CompletableFuture<ServerData> getServerData() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new ServerData((JsonObject) JsonParser.parseString(getURL(getRoute(APIRoute.SERVER_INFO))));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new ServerData();
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
                return new NewsData();
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

    public static CompletableFuture<APIData> API() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new APIData((JsonObject) JsonParser.parseString(
                    getURL("https://raw.githubusercontent.com/EarthMC-Stats/EarthMCEssentials" +
                           "/main/src/main/resources/api.json")));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new APIData();
            }
        });
    }

    public static String getRoute(APIRoute routeType) {
        // TODO: Do this once on startup
        API().thenAccept(data -> apiData = data);

        String route = switch(routeType) {
            case TOWNLESS -> apiData.routes.townless;
            case NATIONS -> apiData.routes.nations;
            case RESIDENTS -> apiData.routes.residents;
            case PLAYERS -> apiData.routes.players;
            case TOWNS -> apiData.routes.towns;
            case ALLIANCES -> apiData.routes.alliances;
            case NEARBY -> apiData.routes.nearby;
            case SERVER_INFO -> apiData.routes.serverInfo;
            case NEWS -> apiData.routes.news;
        };

        route = apiData.getDomain() + route + "/";
        Messaging.sendDebugMessage("GETTING ROUTE - " + route);

        return route;
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
