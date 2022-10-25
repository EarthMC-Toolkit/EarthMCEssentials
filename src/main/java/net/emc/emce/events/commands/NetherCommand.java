package net.emc.emce.events.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.emc.emce.utils.Translation;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;

public class NetherCommand {
    public void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("nether").then(
                            ClientCommandManager.argument("x", IntegerArgumentType.integer()).then(
                                    ClientCommandManager.argument("z", IntegerArgumentType.integer()).executes(c -> {

                                        int x = IntegerArgumentType.getInteger(c, "x");
                                        int z = IntegerArgumentType.getInteger(c, "z");

                                        Messaging.send(Translation.of("msg_nether_success", x / 8, z / 8));

                                        return 1;
                                    })
                            ).executes(c -> {
                                Messaging.send(Translation.of("msg_nether_err_args"));
                                return 1;
                            })).executes(c -> {
                        int x, z;

                        if (MinecraftClient.getInstance().player != null) {
                            x = MinecraftClient.getInstance().player.getBlockX();
                            z = MinecraftClient.getInstance().player.getBlockZ();

                            Messaging.sendPrefixed(Translation.of("msg_nether_owncoords"));
                            Messaging.sendPrefixed(Translation.of("msg_nether_success", x / 8, z / 8));
                        } else Messaging.send(Translation.of("msg_nether_err_null"));

                        return 1;
                    })
            );
        });
    }
}