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
			// Create renderer
            TextRenderer renderer = MinecraftClient.getInstance().textRenderer;

			// Draw each player with offset from player before (will use for loop in future)
            renderer.draw("TownlessPlayer1", 1, 5, 0xffffff);
			renderer.draw("TownlessPlayer2", 1, 15, 0xffffff);
			renderer.draw("TownlessPlayer3", 1, 25, 0xffffff);
        });
	}
}