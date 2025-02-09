package GameLayer;

import GameLayer.Rendering.Model.CubeMesh;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Block extends _Object {
    public enum Faces {
        FRONT,
        BACK,
        RIGHT,
        LEFT,
        TOP,
        BOTTOM
    }

    BlockType type;
    public enum BlockType {
        AIR,
        DIRT,
        GRASS
    }

    public Block() {
        super();
    }
    public Block(String texture) {
        super(texture);
    }
    public Block(String texture, Vector3f position) {
        super(texture, position);
    }
    public Block(String texture, Vector3f position, Vector3f rotation) {
        super(texture, position, rotation);
    }
    public Block(String texture, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(texture, position, rotation, scale);
    }

    public float getTextureIndex(int x) {
        if(x > 5) {
            throw new IllegalArgumentException("Texture index out of bounds");
        }
        
        float[] xx = new float[6];
        /*
                0 -- FRONT
                1 -- BACK
                2 -- RIGHT
                3 -- LEFT
                4 -- TOP
                5 -- BOTTOM
        */
        switch (type) {
            case AIR:
                // DO nothing since xx is already filled with 0
                break;
            case DIRT:
                // DO nothing since xx is already filled with 0
                break;
            case GRASS:
                xx[0] = 0.f;
                xx[1] = 0.f;
                xx[2] = 0.f;
                xx[3] = 0.f;
                xx[4] = 1.f;
                xx[5] = 0.f;
                break;
            default:
                System.out.println("Unknown block type: " + type);
                break;
        }
        return xx[x];
    }
}
