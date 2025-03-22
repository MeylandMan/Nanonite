package net.Core.Physics;

import org.joml.Vector3d;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL40.*;

public class CubeCollision {
    public Vector3d position;
    public Vector3d min;
    public Vector3d max;
    public Vector3d size;

    public CubeCollision(Vector3d position, Vector3d size) {
        this.min = new Vector3d(Math.min(0, size.x), Math.min(0, size.y), Math.min(0, size.z));
        this.max = new Vector3d(Math.max(0, size.x), Math.max(0, size.y), Math.max(0, size.z));
        this.position = position;
        this.size = size;
    }

    public CubeCollision() {}

    public boolean intersects(CubeCollision other) {
        return this.max.x + position.x > other.min.x + other.position.x && this.min.x + position.x < other.max.x  + other.position.x&&
                this.max.y + position.y > other.min.y + other.position.y && this.min.y + position.y < other.max.y + other.position.y&&
                this.max.z + position.z > other.min.z + other.position.z && this.min.z + position.z < other.max.z + other.position.z;
    }

    public boolean contains(Vector3f point) {
        return point.x >= min.x && point.x <= max.x &&
                point.y >= min.y && point.y <= max.y &&
                point.z >= min.z && point.z <= max.z;
    }

    public void expandToInclude(Vector3f point) {
        min.x = Math.min(min.x, point.x);
        min.y = Math.min(min.y, point.y);
        min.z = Math.min(min.z, point.z);
        max.x = Math.max(max.x, point.x);
        max.y = Math.max(max.y, point.y);
        max.z = Math.max(max.z, point.z);
    }

    @Override
    public String toString() {
        return "AABB3D[min=" + min + ", max=" + max + "]";
    }

    public void drawAABB() {
        glDisable(GL_BLEND);
        glDrawArrays(GL_LINE_LOOP, 0, 36);
    }

    public static Vector3d getIntersectionCenter(CubeCollision a, CubeCollision b) {
        double interMinX = Math.max(a.min.x, b.min.x);
        double interMinY = Math.max(a.min.y, b.min.y);
        double interMinZ = Math.max(a.min.z, b.min.z);

        double interMaxX = Math.min(a.max.x, b.max.x);
        double interMaxY = Math.min(a.max.y, b.max.y);
        double interMaxZ = Math.min(a.max.z, b.max.z);

        return new Vector3d(
                (interMinX + interMaxX) / 2.0f,
                (interMinY + interMaxY) / 2.0f,
                (interMinZ + interMaxZ) / 2.0f
        );
    }

    public Vector3d getCenter() {
        return new Vector3d(
                (min.x + max.x) / 2.0f,
                (min.y + max.y) / 2.0f,
                (min.z + max.z) / 2.0f
        );
    }
    
}
