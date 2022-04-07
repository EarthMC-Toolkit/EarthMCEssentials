package net.emc.emce.events.commands;

import net.emc.emce.caches.ServerDataCache;
import net.emc.emce.object.Translation;
import net.emc.emce.utils.Messaging;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.kyori.adventure.text.format.NamedTextColor;

public class QueueCommand {
    public void register() {
        ClientCommandManager.DISPATCHER.register(ClientCommandManager.literal("queuesize").executes(source -> {
            ServerDataCache.INSTANCE.getCache().thenAccept(serverData -> {
                if (serverData == null)
                    Messaging.sendMessage(Translation.of("msg_queue_err"));
                else
                    Messaging.sendMessage(Translation.of("msg_queue_success", serverData.getQueue()).color(NamedTextColor.AQUA));
            });

            return 1;
        }));
    }
}
