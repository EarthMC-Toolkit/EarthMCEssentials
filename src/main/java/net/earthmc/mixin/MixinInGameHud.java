package net.fabricmc.fabric.mixin.client.rendering;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.hud.InGameHud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

@Mixin(InGameHud.class)
public class MixinInGameHud
{
	@Inject(method = "render", at = @At(value = "RETURN", shift = At.Shift.BY, by = -6))
	public void render(float tickDelta, CallbackInfo callbackInfo) 
	{
		HudRenderCallback.EVENT.invoker().onHudRender(tickDelta);
	}
}