package GameLayer;

import GameLayer.Rendering.Model.CubeMesh;
import org.joml.*;


public class Block extends Object {
    public enum BlockType {

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
