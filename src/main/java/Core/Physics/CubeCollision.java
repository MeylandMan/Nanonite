package Core.Physics;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class CubeCollision {
    public Vector3f position;
    public Vector3f min;
    public Vector3f max;

    public CubeCollision(Vector3f position, Vector3f size) {
        this.min = new Vector3f(Math.min(0, size.x), Math.min(0, size.y), Math.min(0, size.z));
        this.max = new Vector3f(Math.max(0, size.x), Math.max(0, size.y), Math.max(0, size.z));
        this.position = position;
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


    Vector3f[] getVertices() {
        return new Vector3f[]{
                new Vector3f(min.x, min.y, min.z), // v0
                new Vector3f(max.x, min.y, min.z), // v1
                new Vector3f(max.x, max.y, min.z), // v2
                new Vector3f(min.x, max.y, min.z), // v3
                new Vector3f(min.x, min.y, max.z), // v4
                new Vector3f(max.x, min.y, max.z), // v5
                new Vector3f(max.x, max.y, max.z), // v6
                new Vector3f(min.x, max.y, max.z)  // v7
        };
    }

    public void drawAABB() {
        Vector3f[] vertices = getVertices();

        glColor3f(1, 1,1);
        glBegin(GL_LINES);

        // Bas
        drawLine(vertices[0], vertices[1]);
        drawLine(vertices[1], vertices[2]);
        drawLine(vertices[2], vertices[3]);
        drawLine(vertices[3], vertices[0]);

        // Haut
        drawLine(vertices[4], vertices[5]);
        drawLine(vertices[5], vertices[6]);
        drawLine(vertices[6], vertices[7]);
        drawLine(vertices[7], vertices[4]);

        // Liens verticaux
        drawLine(vertices[0], vertices[4]);
        drawLine(vertices[1], vertices[5]);
        drawLine(vertices[2], vertices[6]);
        drawLine(vertices[3], vertices[7]);

        glEnd();
    }

    private void drawLine(Vector3f v1, Vector3f v2) {
        glVertex3f(v1.x + position.x, v1.y + position.y, v1.z + position.z);
        glVertex3f(v2.x + position.x, v2.y + position.y, v2.z + position.z);
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

    public static void resolveCollision(CubeCollision blockA, CubeCollision blockB, float deltaTime) {
        if (!blockA.intersects(blockB)) return; // Pas de collision

    }

    public Vector3f getCenter() {
        return new Vector3f(
                (min.x + max.x) / 2.0f,
                (min.y + max.y) / 2.0f,
                (min.z + max.z) / 2.0f
        );
    }
    
}
