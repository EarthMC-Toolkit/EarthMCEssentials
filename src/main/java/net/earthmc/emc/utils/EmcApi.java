package net.earthmc.emc.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.earthmc.emc.EMCMod;
import net.earthmc.emc.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@SuppressWarnings("ThrowablePrintedToSystemOut")
public class EmcApi
{
    public static JsonObject getMapData()
    {
        try
        {
            final URL url = new URL("https://earthmc.net/map/tiles/_markers_/marker_earth.json");

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200)
            {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline.append(scanner.nextLine());
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                return (JsonObject) parse.parse(inline.toString());
            }
        }
        catch (Exception ignore) {
            return new JsonObject();
        }

        return new JsonObject();
    }

    public static JsonObject getPlayerData()
    {
        try
        {
            final URL url = new URL("https://earthmc.net/map/up/world/earth/");

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200)
            {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline.append(scanner.nextLine());
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                return (JsonObject) parse.parse(inline.toString());
            }
        }
        catch (Exception ignore) {
            return new JsonObject();
        }

        return new JsonObject();
    }

    public static JsonArray getTownless()
    {
        try
        {
            final URL url = new URL("http://earthmc-api.herokuapp.com/townlessplayers");

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200)
            {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline.append(scanner.nextLine());
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                return (JsonArray) parse.parse(inline.toString());
            }
        }
        catch (Exception ignore) {
            return new JsonArray();
        }

        return new JsonArray();
    }

    public static JsonArray getNearby(ModConfig config)
    {
        try
        {
            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;
            final URL url;

            if (player != null)
            {
                url = new URL("http://earthmc-api.herokuapp.com/nearby/" + (int) player.getX() + "/" + (int) player.getZ() + "/" + config.nearby.xBlocks + "/" + config.nearby.zBlocks);

                final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                // Getting the response code
                final int responsecode = conn.getResponseCode();

                if (responsecode == 200) {
                    StringBuilder inline = new StringBuilder();
                    final Scanner scanner = new Scanner(url.openStream());

                    // Write all the JSON data into a string using a scanner
                    while (scanner.hasNext()) {
                        inline.append(scanner.nextLine());
                    }

                    // Close the scanner
                    scanner.close();

                    // Using the JSON simple library parse the string into a json object
                    final JsonParser parse = new JsonParser();
                    JsonArray array = (JsonArray) parse.parse(inline.toString());

                    for (int i = 0; i < array.size(); i++) {
                        JsonObject currentObj = (JsonObject) array.get(i);
                        if (currentObj.get("name").getAsString().equals(EMCMod.clientName)) array.remove(i);
                    }
                    return array;
                }
            }
        }
        catch (Exception ignore) {
            return new JsonArray();
        }

        return new JsonArray();
    }

    public static JsonObject getResident(String residentName)
    {
        try
        {
            final URL url = new URL("http://earthmc-api.herokuapp.com/residents/" + residentName);

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200)
            {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline.append(scanner.nextLine());
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                return (JsonObject) parse.parse(inline.toString());
            }
        }
        catch (Exception ignore) {
            return new JsonObject();
        }

        return new JsonObject();
    }

    public static JsonObject getTown(String townName)
    {
        try
        {
            final URL url = new URL("http://earthmc-api.herokuapp.com/towns/" + townName);

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200)
            {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline.append(scanner.nextLine());
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                return (JsonObject) parse.parse(inline.toString());
            }
        }
        catch (Exception ignore) {
            return new JsonObject();
        }

        return new JsonObject();
    }

    public static JsonObject getNation(String nationName)
    {
        try
        {
            final URL url = new URL("http://earthmc-api.herokuapp.com/nations/" + nationName);

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200)
            {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline.append(scanner.nextLine());
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                return (JsonObject) parse.parse(inline.toString());
            }
        }
        catch (Exception ignore) {
            return new JsonObject();
        }

        return new JsonObject();
    }

    public static JsonArray getTowns()
    {
        try
        {
            final URL url = new URL("http://earthmc-api.herokuapp.com/towns/");

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200)
            {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline.append(scanner.nextLine());
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                return (JsonArray) parse.parse(inline.toString());
            }
        }
        catch (Exception ignore)
        {
            return new JsonArray();
        }

        return new JsonArray();
    }

    public static JsonObject getServerInfo() 
    {
        try 
        {
            final URL url = new URL("http://earthmc-api.herokuapp.com/serverinfo/");

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            // Getting the response code
            final int responsecode = conn.getResponseCode();

            if (responsecode == 200)
            {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                // Write all the JSON data into a string using a scanner
                while (scanner.hasNext())
                {
                    inline.append(scanner.nextLine());
                }

                // Close the scanner
                scanner.close();

                // Using the JSON simple library parse the string into a json object
                final JsonParser parse = new JsonParser();
                return (JsonObject) parse.parse(inline.toString());
            }
        }
        catch (Exception ignore) {
            return new JsonObject();
        } 
        return new JsonObject();
    }

    public static JsonArray getNations() {
        try {
            final URL url = new URL("http://earthmc-api.herokuapp.com/nations/");

            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() == 200) {
                StringBuilder inline = new StringBuilder();
                final Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) 
                    inline.append(scanner.nextLine());

                scanner.close();

                final JsonParser parse = new JsonParser();
                return (JsonArray) parse.parse(inline.toString());
            }
        } catch (Exception ignore) {
            return new JsonArray();
        }
        return new JsonArray();
    }
}
