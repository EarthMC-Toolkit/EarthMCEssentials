package net.earthmc.emc;

import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.fabricmc.api.EnvType;

@Environment(EnvType.CLIENT)
class ModMenuIntegration implements ModMenuApi 
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> (Screen) AutoConfig.getConfigScreen(ModConfig.class, parent);
    }

    @Override
    public String getModId() 
    {
        return "emc-essentials";
    }
}