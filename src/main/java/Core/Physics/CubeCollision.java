package Core.Physics;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL40.*;

public class CubeCollision {
    public Vector3f position;
    public Vector3f min;
    public Vector3f max;
    public Vector3f size;

    public CubeCollision(Vector3f position, Vector3f size) {
        this.min = new Vector3f(Math.min(0, size.x), Math.min(0, size.y), Math.min(0, size.z));
        this.max = new Vector3f(Math.max(0, size.x), Math.max(0, size.y), Math.max(0, size.z));
        this.position = position;
        this.size = size;
    }

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

    public static Vector3f getIntersectionCenter(CubeCollision a, CubeCollision b) {
        float interMinX = Math.max(a.min.x, b.min.x);
        float interMinY = Math.max(a.min.y, b.min.y);
        float interMinZ = Math.max(a.min.z, b.min.z);

        float interMaxX = Math.min(a.max.x, b.max.x);
        float interMaxY = Math.min(a.max.y, b.max.y);
        float interMaxZ = Math.min(a.max.z, b.max.z);

        return new Vector3f(
                (interMinX + interMaxX) / 2.0f,
                (interMinY + interMaxY) / 2.0f,
                (interMinZ + interMaxZ) / 2.0f
        );
    }

    public Vector3f getCenter() {
        return new Vector3f(
                (min.x + max.x) / 2.0f,
                (min.y + max.y) / 2.0f,
                (min.z + max.z) / 2.0f
        );
    }
    
}
