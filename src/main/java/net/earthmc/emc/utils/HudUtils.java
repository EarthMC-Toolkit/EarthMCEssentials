package net.earthmc.emc.utils;

public class HudUtils
{
    public enum State
    {
        BOTTOM_LEFT("BOTTOM_LEFT", 5, 375),
        BOTTOM_MIDDLE("BOTTOM_MIDDLE", 450, 375),
        BOTTOM_RIGHT("BOTTOM_RIGHT", 770, 375),
        LEFT("LEFT", 0, 0),
        RIGHT("RIGHT", 0, 0),
        TOP_LEFT("TOP_LEFT", 5, 16),
        TOP_MIDDLE("TOP_MIDDLE", 0, 0),
        TOP_RIGHT("TOP_RIGHT", 0, 0);

        private final String name;
        private final int x;
        private final int y;

        State(String name, int x, int y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        public String getName() {
            return name;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
