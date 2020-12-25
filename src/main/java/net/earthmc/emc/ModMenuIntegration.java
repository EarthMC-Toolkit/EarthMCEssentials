package net.earthmc.emc;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.earthmc.emc.utils.ConfigUtils;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> ConfigUtils.getConfigBuilder().build();
    }

    @Override
    public String getModId() {
        return "emc-essentials";
    }
}