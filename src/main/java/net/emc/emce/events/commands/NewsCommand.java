package net.emc.emce.events.commands;

import com.google.gson.JsonObject;

import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public record NewsCommand(EMCEssentials instance) implements ICommand {
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("news")
            .then(ClientCommandManager.literal("summary").executes(ctx -> execNews(20)))
            .then(ClientCommandManager.literal("latest").executes(ctx -> execNews(1)))
            .then(ClientCommandManager.argument("amount", IntegerArgumentType.integer(1)).executes(ctx ->
                execNews(ctx.getArgument("amount", int.class))
            ));
    }
    
    // Prints last X headlines with their respective date.
    public int execNews(int count) {
        try {
            Messaging.createAndSend(
                "text_news_header", NamedTextColor.BLUE,
                Component.text(count, NamedTextColor.WHITE)
            );
            
            sendHeadlines(count);
        } catch (Exception e) {
            String err = String.format("Error getting the last %d headline(s) from news.", count);
            System.err.println(err + "\n" + e);
            Messaging.sendDebugMessage(err, e);
        }
        
        return 1;
    }
    
    /**
     * Sends news messages to the chat in the following format:<br>
     * <code>
     * <b>(16/3/2025) - Example bogus news headline.</b>
     * </code>
     * @param count The max amount of headlines to send.
     */
    public void sendHeadlines(int count) throws Exception {
        Map<Long, String> newsMsgs = parseNewsMessages(count);
        if (newsMsgs.isEmpty()) {
            throw new Exception("Failed to parse news messages! Returned empty map.");
        }
        
        // TODO: Reduce complexity, just do this in single loop via parseNewsMessages.
        List<Component> headlines = formatNewsMessages(newsMsgs);
        if (headlines.isEmpty()) {
            throw new Exception("Failed to format news messages! Returned empty list.");
        }
        
        // Send a message for every headline.
        for (Component headline : headlines) {
            Messaging.send(headline);
        }
    }
    
    // Key is timestamp, Value is headline.
    Map<Long, String> parseNewsMessages(int count) {
        // We don't need TreeMap here since NewsDataCache should be in order already.
        Map<Long, String> headlines = new LinkedHashMap<>();
        
        // When count > size, limit will do nothing and just include every msg.
        NewsDataCache.INSTANCE.getCache()
            .stream().limit(count)
            .forEach(el -> {
                JsonObject newsMsg = el.getAsJsonObject();
                String headline = newsMsg.get("headline").getAsString();
                long timestamp = newsMsg.get("timestamp").getAsLong();
                
                headlines.put(timestamp, headline);
            });
        
        return headlines;
    }
    
    // Convert timestamp/headline pairs into text components with styling.
    public List<Component> formatNewsMessages(Map<Long, String> msgs) {
        List<Component> textComponents = new ArrayList<>();
        
        for (Map.Entry<Long, String> msg : msgs.entrySet()) {
            String formattedDate = timestampToDate(msg.getKey());
            String headline = msg.getValue();
            
            Component comp = Messaging.create(
                "text_news_headline", NamedTextColor.GOLD,
                Component.text(formattedDate),
                Component.text("-").color(NamedTextColor.WHITE),
                Component.text(headline).color(NamedTextColor.AQUA)
            );
            
            textComponents.add(comp);
        }
        
        return textComponents;
    }
    
    /**
     * Turns a timestamp into a date in the format: <code>16/3/25</code>
     * @param timestamp Sec/ms elapsed since Unix epoch (same as Discord).
     * @return The formatted date as a string.
     */
    public static String timestampToDate(long timestamp) {
        ZonedDateTime zdt = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault());

        // Define the formatter based on the user's locale
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yy", Locale.getDefault());
        return zdt.format(formatter);
    }
}