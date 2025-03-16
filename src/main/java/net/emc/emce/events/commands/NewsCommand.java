package net.emc.emce.events.commands;

import com.google.gson.JsonObject;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.emc.emce.EMCEssentials;
import net.emc.emce.caches.NewsDataCache;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public record NewsCommand(EMCEssentials instance) implements ICommand {
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("news")
            .then(ClientCommandManager.literal("latest").executes(c -> execNewsLatest()))
            .then(ClientCommandManager.literal("summary").executes(c -> execNewsSummary()));
    }
    
    // Prints the latest headline and the date it was reported at.
    public int execNewsLatest() {
        try {
            execNewsAmt(1);
        } catch (Exception e) {
            System.err.println("Error getting the latest headline from news.\n" + e);
        }
        
        return 1;
    }
    
    // Prints last 10 headlines with their respective date.
    public int execNewsSummary() {
        try {
            execNewsAmt(10);
        } catch (Exception e) {
            System.err.println("Error getting the last 10 headlines from news.\n" + e);
        }
        
        return 1;
    }
    
    /**
     * Sends news messages to the chat in the following format:<br>
     * <code>
     * <b>X nation has done something in response to Y nation.</b> (Reported: 16/3/2025)
     * </code>
     * @param count The max amount of headlines to send.
     */
    public void execNewsAmt(int count) throws Exception {
        Map<Long, String> newsMsgs = parseNewsMessages(count);
        if (newsMsgs.isEmpty()) {
            throw new Exception("Failed to parse news messages! Returned empty map.");
        }
        
        List<String> headlines = formatNewsMessages(newsMsgs);
        if (headlines.isEmpty()) {
            throw new Exception("Failed to format news messages! Returned empty list.");
        }
        
        String output = String.join("\n", headlines);
        Messaging.send(Component.text(output, NamedTextColor.AQUA));
    }
    
    // Key is timestamp, Value is headline.
    Map<Long, String> parseNewsMessages(int count) {
        Set<JsonObject> news = NewsDataCache.INSTANCE.getCache().values()
            .stream().limit(count)
            .collect(Collectors.toSet());

        Map<Long, String> headlines = new HashMap<>();
        for (JsonObject newsMsg : news) {
            String headline = newsMsg.get("headline").getAsString();
            long timestamp = newsMsg.get("timestamp").getAsLong();
            
            headlines.put(timestamp, headline);
        }
        
        return headlines;
    }
    
    // Convert timestamp/headline pairs into single strings.
    public List<String> formatNewsMessages(Map<Long, String> msgs) {
        List<String> formattedMessages = new ArrayList<>();
        msgs.forEach((timestamp, headline) -> {
            String formattedDate = formatDateFromTimestamp(timestamp);
            String formattedMessage = String.format("%s (Reported: %s)", headline, formattedDate);
            
            formattedMessages.add(formattedMessage);
        });
        
        return formattedMessages;
    }
    
    public static String formatDateFromTimestamp(long timestamp) {
        ZonedDateTime zdt = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault());

        // Define the formatter based on the user's locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy", Locale.getDefault());
        return zdt.format(formatter);
    }
}