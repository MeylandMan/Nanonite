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
        VOID,
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
    public Block(Vector3f position) {
        super(position);
    }
    public Block(Vector3f position, Vector3f rotation) {
        super(position, rotation);
    }
    public Block(Vector3f position, Vector3f rotation, Vector3f scale) {
        super(position, rotation, scale);
    }

}
