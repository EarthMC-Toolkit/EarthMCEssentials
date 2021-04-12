package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.emc.emce.EMCE;
import net.emc.emce.exception.APIException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class EmcApi {
    public static JsonArray getTownless() {
        try {
            return (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/townlessplayers").toString());
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return EMCE.townless;
        }
    }

    public static JsonArray getNearby(int xBlocks, int zBlocks) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            
            if (player != null) {
                if (!player.getEntityWorld().getDimension().isBedWorking())
                    return new JsonArray();
                
                JsonArray array = (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/nearby/" +
                        (int) player.getX() + "/" +
                        (int) player.getZ() + "/" +
                        xBlocks + "/" + zBlocks).toString());

                for (int i = 0; i < array.size(); i++) {
                    JsonObject currentObj = (JsonObject) array.get(i);
                    if (currentObj.get("name").getAsString().equals(client.player.getName().asString())) array.remove(i);
                }
                return array;
            } else
                return EMCE.nearby;
        }
        catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return EMCE.nearby;
        }        
    }

    public static JsonObject getResident(String residentName) {
        try {
            return (JsonObject) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/residents/" + residentName).toString());
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return new JsonObject();
        }
    }

    public static JsonArray getTowns() {
        try {
            return (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/towns/").toString());
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return EMCE.allTowns;
        }        
    }

    public static JsonObject getServerInfo() {
        try {
            return (JsonObject) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/serverinfo/").toString());
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return new JsonObject();
        }
    }

    public static JsonArray getNations() {
        try {
            return (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/nations/").toString());
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return EMCE.allNations;
        }
    }

    private static StringBuilder getURL(String urlString) throws APIException {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) 
                    inline.append(scanner.nextLine());

                scanner.close();

                return inline;
            } else
                throw new APIException("API returned response code " + connection.getResponseCode() + " for URL: " + urlString);
        } catch (Exception e) {
            throw new APIException(e.getMessage());
        }
    }
}
