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
                if (serverData == null) Messaging.send(Translation.of("msg_queue_err"));
                else Messaging.send(Messaging.create("msg_queue_success", NamedTextColor.AQUA, serverData.getQueue()));
            });

            return 1;
        }));
    }
}
