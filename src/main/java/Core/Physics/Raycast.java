package Core.Physics;


import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class Raycast {
    public Vector3f origin;
    public Vector3f direction;

    public Raycast(Vector3f origin, Vector3f direction) {
        this.origin = new Vector3f(origin);
        this.direction = new Vector3f(direction);
    }

    /**
     * Teste si le rayon intersecte une AABB
     * @param aabb La boîte AABB à tester
     * @return Distance de l'impact, ou -1 si pas d'intersection
     */
    public float intersectCube(CubeCollision aabb) {
        Vector3f min = aabb.min.add(aabb.position);
        Vector3f max = aabb.max.add(aabb.position);

        float tMin = (min.x - origin.x) / direction.x;
        float tMax = (max.x - origin.x) / direction.x;

        if (tMin > tMax) {
            float temp = tMin;
            tMin = tMax;
            tMax = temp;
        }

        float tyMin = (min.y - origin.y) / direction.y;
        float tyMax = (max.y - origin.y) / direction.y;

        if (tyMin > tyMax) {
            float temp = tyMin;
            tyMin = tyMax;
            tyMax = temp;
        }

        if ((tMin > tyMax) || (tyMin > tMax))
            return -1;

        if (tyMin > tMin)
            tMin = tyMin;
        if (tyMax < tMax)
            tMax = tyMax;

        float tzMin = (min.z - origin.z) / direction.z;
        float tzMax = (max.z - origin.z) / direction.z;

        if (tzMin > tzMax) {
            float temp = tzMin;
            tzMin = tzMax;
            tzMax = temp;
        }

        if ((tMin > tzMax) || (tzMin > tMax))
            return -1; // Pas d'intersection

        if (tzMin > tMin)
            tMin = tzMin;
        if (tzMax < tMax)
            tMax = tzMax;

        return (tMin < 0) ? tMax : tMin; // Distance de l'impact
    }

    /**
     * Retourne le point d'impact sur une AABB
     * @param aabb La boîte AABB
     * @return Le point d'impact (ou null si pas d'intersection)
     */
    public Vector3f getImpactPoint(CubeCollision aabb) {
        float t = intersectCube(aabb);
        if (t < 0) return null;
        return new Vector3f(origin).add(direction.mul(t));
    }

    public void drawRay(float length) {
        Vector3f end = new Vector3f(origin).add(new Vector3f(direction).mul(length));

        glColor3f(1.0f, 0.0f, 0.0f);
        glBegin(GL_LINES);

        // Point de départ du rayon
        glVertex3f(origin.x, origin.y, origin.z);

        // Point final du rayon
        glVertex3f(end.x, end.y, end.z);

        glEnd();
    }
}

