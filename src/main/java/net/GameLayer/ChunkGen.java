package net.GameLayer;

public class ChunkGen {
    public final static byte X_DIMENSION = 16;
    public final static short Y_DIMENSION = 255;
    public final static byte Z_DIMENSION = 16;
    public final static int Y_MAX = 5;


    public enum BlockType {
        DIRT((byte)0),
        GRASS((byte)1);

        private final byte id;

        BlockType(byte id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }

    public void AddSurface() {

    }


}
