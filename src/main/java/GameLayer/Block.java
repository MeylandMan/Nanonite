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
        DIRT
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
}
