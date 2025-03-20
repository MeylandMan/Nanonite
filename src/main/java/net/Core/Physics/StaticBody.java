package net.Core.Physics;

import org.joml.Vector3d;
import org.joml.Vector3f;

public class StaticBody extends CubeCollision {
    public StaticBody(Vector3d position, Vector3d size) {
        super(position, size);
    }
}
