package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.config.ModConfig;
import net.emc.emce.object.Resident;
import net.emc.emce.object.exception.APIException;
import net.emc.emce.object.ServerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class EarthMCAPI {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ModConfig config = ModConfig.instance();

    public static CompletableFuture<JsonArray> getTownless() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) new JsonParser().parse(getURL(config.api.domain + config.api.townlessRoute));
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

                    JsonArray array = (JsonArray) new JsonParser().parse(getURL(config.api.domain + config.api.nearbyRoute +
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
                return new Resident((JsonObject) new JsonParser().parse(getURL(config.api.domain + config.api.residentsRoute + residentName)));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new Resident(residentName);
            }
        });
    }

    public static CompletableFuture<JsonArray> getTowns() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) new JsonParser().parse(getURL(config.api.domain + config.api.townsRoute));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }

    public static CompletableFuture<ServerData> getServerData() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return new ServerData((JsonObject) new JsonParser().parse(getURL(config.api.domain + config.api.serverInfoRoute)));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new ServerData();
            }
        });
    }

    public static CompletableFuture<JsonArray> getNations() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return (JsonArray) new JsonParser().parse(getURL(config.api.domain + config.api.nationsRoute));
            } catch (APIException e) {
                MsgUtils.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }

    private static String getURL(String urlString) throws APIException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() != 200)
                throw new APIException("API returned response code " + response.statusCode() + " for URL: " + urlString);

            return response.body();
        } catch (Exception e) {
            throw new APIException(e.getMessage());
        }
    }
}
