package net.Core.Physics;


import org.joml.Vector3d;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class Raycast {
    public Vector3d origin;
    public Vector3d direction;

    public Raycast(Vector3d origin, Vector3d direction) {
        this.origin = new Vector3d(origin);
        this.direction = new Vector3d(direction);
    }

    public void update(Vector3d origin, Vector3d direction) {
        this.origin = new Vector3d(origin);
        this.direction = new Vector3d(direction);
    }

    /**
     * Teste si le rayon intersecte une AABB
     * @param aabb La boîte AABB à tester
     * @return Distance de l'impact, ou -1 si pas d'intersection
     */
    public double intersectCube(CubeCollision aabb) {
        Vector3d min = aabb.min.add(aabb.position);
        Vector3d max = aabb.max.add(aabb.position);

        double tMin = (min.x - origin.x) / direction.x;
        double tMax = (max.x - origin.x) / direction.x;

        if (tMin > tMax) {
            double temp = tMin;
            tMin = tMax;
            tMax = temp;
        }

        double tyMin = (min.y - origin.y) / direction.y;
        double tyMax = (max.y - origin.y) / direction.y;

        if (tyMin > tyMax) {
            double temp = tyMin;
            tyMin = tyMax;
            tyMax = temp;
        }

        if ((tMin > tyMax) || (tyMin > tMax))
            return -1;

        if (tyMin > tMin)
            tMin = tyMin;
        if (tyMax < tMax)
            tMax = tyMax;

        double tzMin = (min.z - origin.z) / direction.z;
        double tzMax = (max.z - origin.z) / direction.z;

        if (tzMin > tzMax) {
            double temp = tzMin;
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

    public Vector3d getImpactPoint(CubeCollision aabb) {
        double t = intersectCube(aabb);
        if (t < 0) return null;
        return new Vector3d(origin).add(direction.mul(t));
    }

    public void drawRay(float length) {

    }
}

