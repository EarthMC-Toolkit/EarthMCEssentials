package net.earthmc.emc.utils;

public class HudUtils
{
    public enum State
    {
        BOTTOM_LEFT("BOTTOM_LEFT", 5, 375, false),
        BOTTOM_MIDDLE("BOTTOM_MIDDLE", 450, 375, false),
        BOTTOM_RIGHT("BOTTOM_RIGHT", 800, 375, false),
        LEFT("LEFT", 5, 250, false),
        RIGHT("RIGHT", 800, 250, false),
        TOP_LEFT("TOP_LEFT", 5, 16, false),
        TOP_MIDDLE("TOP_MIDDLE", 450, 16, false),
        TOP_RIGHT("TOP_RIGHT", 800, 16, false);

        private boolean active;
        private final String name;
        private final int x;
        private final int y;

        State(String name, int x, int y, boolean active) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.active = active;
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

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean a) {
            active = a;
        }
    }
}
