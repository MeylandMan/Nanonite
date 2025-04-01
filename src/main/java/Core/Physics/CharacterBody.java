package Core.Physics;

import org.joml.Vector3d;

public class CharacterBody extends CubeCollision {

    public CharacterBody() { super(); }
    public CharacterBody(Vector3d position, Vector3d size) { super(position, size); }
}
