package net.GameLayer;

public class ChunkGen {
    public final static byte X_DIMENSION = 16;
    public final static short Y_DIMENSION = 255;
    public final static byte Z_DIMENSION = 16;
    public final static int Y_MAX = 5;

    public enum BlockType {
        DIRT(0, "dirt"),
        GRASS(1, "grass_block");

        private final int id;
        private final String name;

        BlockType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getID() {
            return id;
        }

        public String getName() {
            return name;
        }
    }


    public void AddSurface() {

    }


}
