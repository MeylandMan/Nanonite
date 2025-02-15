import GameLayer._Object;
import org.joml.Vector3f;

public class Block extends _Object {
    public enum Faces {
        FRONT,
        BACK,
        RIGHT,
        LEFT,
        TOP,
        BOTTOM
    }

    BlockType type = BlockType.AIR;
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
