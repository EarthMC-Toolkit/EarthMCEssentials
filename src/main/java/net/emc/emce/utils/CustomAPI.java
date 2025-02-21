package net.emc.emce.utils;

import com.google.gson.JsonArray;
import io.github.emcw.exceptions.APIException;
import io.github.emcw.utils.http.JSONRequest;
import net.emc.emce.objects.CustomAPI.APIData;
import net.emc.emce.objects.CustomAPI.APIRoute;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CustomAPI {
    public static APIData apiData = new APIData();
    private static final String endpoints =
          "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMCEssentials/main/src/main/resources/api.json";
    
    @Contract(" -> new")
    public static @NotNull CompletableFuture<JsonArray> getAlliances() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return JSONRequest.sendGet(getRoute(APIRoute.ALLIANCES));
            } catch (APIException e) {
                Messaging.sendDebugMessage(e.getMessage(), e);
                return new JsonArray();
            }
        });
    }
}