package net.emc.emce.objects;

import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum Colors {
    BLUE(NamedTextColor.BLUE),
    DARK_BLUE(NamedTextColor.DARK_BLUE),
    GREEN(NamedTextColor.GREEN),
    DARK_GREEN(NamedTextColor.DARK_GREEN),
    AQUA(NamedTextColor.AQUA),
    DARK_AQUA(NamedTextColor.DARK_AQUA),
    RED(NamedTextColor.RED),
    DARK_RED(NamedTextColor.DARK_RED),
    LIGHT_PURPLE(NamedTextColor.LIGHT_PURPLE),
    DARK_PURPLE(NamedTextColor.DARK_PURPLE),
    YELLOW(NamedTextColor.YELLOW),
    GOLD(NamedTextColor.GOLD),
    GRAY(NamedTextColor.GRAY),
    DARK_GRAY(NamedTextColor.DARK_GRAY),
    BLACK(NamedTextColor.BLACK),
    WHITE(NamedTextColor.WHITE);

    private final NamedTextColor namedTextColor;

    Colors(NamedTextColor namedTextColor) {
        this.namedTextColor = namedTextColor;
    }

    @NotNull
    public NamedTextColor named() {
        return this.namedTextColor;
    }
}