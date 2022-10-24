package net.emc.emce.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Arrays;
import java.util.List;

public class Translation {
    public static Component of(String key) {
        return Component.translatable(key);
    }

    public static Component of(String key, Object... args) {
        List<TextComponent> components = Arrays.stream(args).map(arg -> Component.text(String.valueOf(arg))).toList();

        return Component.translatable(key, components);
    }
}
