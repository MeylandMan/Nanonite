package GameLayer;

import GameLayer._Object;
import org.joml.Vector3f;

public class Block extends _Object {

    int ID = 0;
    BlockType type = BlockType.DIRT;
    int opacity = 1;

    public enum BlockType {
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
