package net.emc.emce.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.object.Resident;
import net.emc.emce.object.exception.APIException;
import net.emc.emce.object.ServerData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class EarthMCAPI {
    public static JsonArray getTownless() {
        try {
            return (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/townlessplayers"));
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return new JsonArray();
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
                        xBlocks + "/" + zBlocks));

                for (int i = 0; i < array.size(); i++) {
                    JsonObject currentObj = (JsonObject) array.get(i);
                    if (currentObj.get("name").getAsString().equals(client.player.getName().asString())) array.remove(i);
                }
                return array;
            } else
                return EarthMCEssentials.getNearbyPlayers();
        }
        catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return EarthMCEssentials.getNearbyPlayers();
        }        
    }

    public static Resident getResident(String residentName) {
        try {
            return new Resident((JsonObject) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/residents/" + residentName)));
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return new Resident(residentName);
        }
    }

    public static JsonArray getTowns() {
        try {
            return (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/towns/"));
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return EarthMCEssentials.getTowns();
        }        
    }

    public static ServerData getServerData() {
        try {
            return new ServerData((JsonObject) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/serverinfo/")));
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return new ServerData();
        }
    }

    public static JsonArray getNations() {
        try {
            return (JsonArray) new JsonParser().parse(getURL("http://earthmc-api.herokuapp.com/nations/"));
        } catch (APIException e) {
            MsgUtils.sendDebugMessage(e.getMessage(), e);
            return EarthMCEssentials.getNations();
        }
    }

    private static String getURL(String urlString) throws APIException {
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
                connection.disconnect();

                return inline.toString();
            } else
                throw new APIException("API returned response code " + connection.getResponseCode() + " for URL: " + urlString);
        } catch (Exception e) {
            throw new APIException(e.getMessage());
        }
    }
}
