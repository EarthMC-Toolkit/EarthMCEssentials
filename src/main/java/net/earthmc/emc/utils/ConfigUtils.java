package net.earthmc.emc.utils;

import net.earthmc.emc.ModConfig;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigManager;
import me.sargunvohra.mcmods.autoconfig1u.serializer.ConfigSerializer;

public class ConfigUtils
{
    private ConfigUtils(){

    }

    public static void serializeConfig(ModConfig config)
    {
        try 
        {
            ((ConfigManager<ModConfig>) AutoConfig.getConfigHolder(ModConfig.class)).getSerializer().serialize(config);
        } catch (ConfigSerializer.SerializationException serializeException) {
            serializeException.printStackTrace();
        }
    }
}