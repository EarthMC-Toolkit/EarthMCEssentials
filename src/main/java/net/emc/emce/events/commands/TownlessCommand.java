package net.emc.emce.events.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.emc.emce.EMCEssentials;
import net.emc.emce.modules.OverlayRenderer;

import net.emc.emce.utils.Messaging;
import net.emc.emce.utils.ModUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;

import java.util.Set;
import java.util.HashSet;

@SuppressWarnings("SameParameterValue")
public record TownlessCommand(EMCEssentials instance) implements ICommand {
    static NamedTextColor townlessTextColour;
    
    boolean lengthLimited(String str) { return lengthLimited(str, 256); }
    boolean lengthLimited(String str, int length) { return str.length() >= length; }

    String inviteStr(StringBuilder str) { return inviteStr(str.toString()); }
    String inviteStr(String str) { return inviteStr(str, false); }

    String inviteStr(Object str, boolean revoking) {
        return "towny:town invite" + (revoking ? " -" : " ") + str.toString() + " ";
    }

    NamedTextColor getTextColour() { return instance.config().commands.townlessTextColour.named(); }
    TextComponent whiteText(int size) { return Component.text(size).color(NamedTextColor.WHITE); }

    Component createMsg(String key, int size) {
        return Messaging.create(key, getTextColour(), whiteText(size));
    }
    
    public LiteralArgumentBuilder<FabricClientCommandSource> build() {
        return ClientCommandManager.literal("townless").executes(c -> execTownless())
            .then(ClientCommandManager.literal("inviteAll").executes(c -> execInviteAll()))
            .then(ClientCommandManager.literal("revokeAll").executes(c -> execRevokeAll()))
            .then(ClientCommandManager.literal("refresh").executes(c -> execRefresh()))
            .then(ClientCommandManager.literal("clear").executes(c -> execClear()));
    }
    
    public int execTownless() {
        Set<String> townless = instance.getTownless();
        if (townless.isEmpty()) {
            townless = instance.getCurrentMap().Players.getByResidency(false).keySet();
        }

        Messaging.send(createMsg("text_townless_header", townless.size()));
        Messaging.send(Component.text(String.join(", ", townless), townlessTextColour));
        
        return 1;
    }
    
    public int execInviteAll() {
        if (MinecraftClient.getInstance().player == null) return -1;
        
        if (ModUtils.isConnectedToEMC()) {
            StringBuilder townlessString = new StringBuilder();
            Set<String> townless = instance.getTownless();
            
            for (String townlessPlayer : townless) {
                if (lengthLimited(inviteStr(townlessString) + townlessPlayer)) break;
                else townlessString.append(townlessPlayer).append(" ");
            }
            
            Messaging.performCommand("towny:town invite " + townlessString);
            Messaging.sendPrefixed(createMsg("msg_townless_sent", townless.size()));
        } else Messaging.sendPrefixed("msg_townless_invite_err");
        
        return 1;
    }
    
    public int execRevokeAll() {
        if (MinecraftClient.getInstance().player == null) return -1;
        
        if (ModUtils.isConnectedToEMC()) {
            StringBuilder townlessString = new StringBuilder();
            Set<String> townless = instance.getTownless();
            
            for (String townlessPlayer : townless) {
                if (lengthLimited(inviteStr(townlessString, true) + townlessPlayer)) break;
                else townlessString.append("-").append(townlessPlayer).append(" ");
            }
            
            Messaging.performCommand("towny:town invite " + townlessString);
            Messaging.sendPrefixed(createMsg("msg_townless_revoked", townless.size()));
        }
        else Messaging.sendPrefixed("msg_townless_revoke_err");
        
        return 1;
    }
    
    public int execRefresh() {
        instance.updateTownless();
        Messaging.sendPrefixed("msg_townless_refresh");
        
        return 1;
    }
    
    public int execClear() {
        OverlayRenderer.SetTownless(new HashSet<>());
        OverlayRenderer.UpdateStates(true, false);
        
        Messaging.sendPrefixed("msg_townless_clear");
        
        return 1;
    }
}