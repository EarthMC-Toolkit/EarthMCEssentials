package net.emc.emce.events.commands;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.emc.emce.EarthMCEssentials;
import net.emc.emce.caches.TownDataCache;
import net.emc.emce.utils.MsgUtils;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.Locale;
import java.util.Objects;

public class AllianceCommand {
    public static void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("alliance").then(
                ClientCommandManager.argument("allianceName", StringArgumentType.string()).executes(c -> {
                    String allianceName = StringArgumentType.getString(c, "allianceName");

                    // Implement data cache

                    return 1;
                })
        ).executes(c -> {
            FabricClientCommandSource source = c.getSource();

            return 1;
        }));
    }

}
