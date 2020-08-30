package net.earthmc.emc;

import net.fabricmc.api.ModInitializer;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class EMCMod implements ModInitializer 
{
	@Override
	public void onInitialize() // Called when Minecraft starts.
	{
		System.out.println("EarthMC Mod Initialized!");

		HudRenderCallback.EVENT.register(e -> 
		{
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
            renderer.draw("TownlessPlayer1", 0, 0, 0xffffff);
			renderer.draw("TownlessPlayer2", 0, 10, 0xffffff);
			renderer.draw("TownlessPlayer2", 0, 20, 0xffffff);
        });
	}
}