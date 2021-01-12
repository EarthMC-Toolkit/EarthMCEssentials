package net.emc.emce;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.emc.emce.utils.ConfigUtils;

public class ModMenuIntegration implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> ConfigUtils.getConfigBuilder().build();
    }
}