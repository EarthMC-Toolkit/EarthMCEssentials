package net.emc.emce.mixins;

import com.google.gson.JsonElement;

import com.google.gson.JsonObject;
import io.github.emcw.utils.GsonUtil;
import net.emc.emce.EMCEssentials;
import net.emc.emce.utils.OAPIV3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("TAIL"), method="<init>")
    private void onInit(RunArgs args, CallbackInfo ci) {
//        String clientName = args.network.session.getUsername();
//        JsonElement apiPlayer = OAPIV3.getPlayer(EMCEssentials.instance().currentMap, clientName);
//
//        if (apiPlayer != null) {
//            JsonObject clientPlayer = apiPlayer.getAsJsonObject();
//            EMCEssentials.instance().setClientPlayer(clientPlayer);
//
//            JsonObject condensed = new JsonObject();
//            condensed.addProperty("name", clientPlayer.get("name").getAsString());
//            condensed.addProperty("uuid", clientPlayer.get("uuid").getAsString());
//            condensed.add("town", clientPlayer.getAsJsonObject("town").get("name"));
//            condensed.addProperty("balance", clientPlayer.getAsJsonObject("stats").get("balance").getAsFloat());
//
//            System.out.println("EMCE > [onInit] Initialized clientPlayer. Condensed representation:");
//            System.out.println(GsonUtil.serialize(condensed));
//
//            return;
//        }
//
//        System.err.println("EMCE > Could not find player by client name: " + clientName);
    }
}