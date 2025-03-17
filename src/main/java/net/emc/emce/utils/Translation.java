package net.emc.emce.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

import java.util.stream.Stream;

public class Translation {
    public static Component of(String key) {
        return translatable(key);
    }
    
    public static Component of(String key, NamedTextColor colour) {
        return translatable().key(key).color(colour).build();
    }
    
    public static Component of(String key, Object... args) {
        Stream<TextComponent> components = Stream.of(args).parallel().map(arg -> text(String.valueOf(arg)));
        return translatable(key, components.toList());
    }
}