package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.emc.emce.EMCE;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class EmcApi {
    public static JsonArray getTownless() {
        try {
            return (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/townlessplayers").toString());
        } catch (Exception e) {
            return EMCE.townless;
        }
    }

    public static JsonArray getNearby(int xBlocks, int zBlocks) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            
            if (player != null) {
               JsonArray array = (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/nearby/" + (int) player.getX() + "/" + (int) player.getZ() + "/" + xBlocks + "/" + zBlocks).toString());

                for (int i = 0; i < array.size(); i++) {
                    JsonObject currentObj = (JsonObject) array.get(i);
                    if (currentObj.get("name").getAsString().equals(client.player.getName().asString())) array.remove(i);
                }
                return array;
            } else
                return EMCE.nearby;
        }
        catch (Exception ignore) {
            return EMCE.nearby;
        }        
    }

    public static JsonObject getResident(String residentName) {
        try {
            return (JsonObject) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/residents/" + residentName).toString());
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    public static JsonArray getTowns() {
        try {
            return (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/towns/").toString());
        } catch (Exception e) {
            return EMCE.allTowns;
        }        
    }

    public static JsonObject getServerInfo() {
        try {
            return (JsonObject) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/serverinfo/").toString());
        } catch (Exception e) {
            return new JsonObject();
        }
    }

    public static JsonArray getNations() {
        try {
            return (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/nations/").toString());
        } catch (Exception e) {
            return EMCE.allNations;
        }
    }

    private static StringBuilder getURL(String urlString) throws Exception {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() == 200) {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) 
                    inline.append(scanner.nextLine());

                scanner.close();

                return inline;
            } else
                throw new Exception("Invalid response code when getting URL");
        } catch (Exception e) {
            return new StringBuilder();
        }
    }
}
