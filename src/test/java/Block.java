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

    // -1 = AIR
    int ID = -1;
    BlockType type = BlockType.AIR;
    int opacity = 0;

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
    public void setType(BlockType type) {
        this.type = type;
        switch (type) {
            case AIR:
                ID = -1;
                opacity = 0;
                break;
            case DIRT:
                ID = 0;
                opacity = 1;
                break;
            case GRASS:
                ID = 1;
                opacity = 1;
                break;
        }
    }
}
