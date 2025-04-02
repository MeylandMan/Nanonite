package Mycraft.Core;


import GameLayer.ChunkGen;
import GameLayer.World;

import static org.joml.Math.*;
import static org.joml.Math.toRadians;

import org.joml.*;

import java.lang.Math;
import java.util.*;

public class Camera {
    public enum Camera_Movement {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }

    public static class Plane {
        public double a, b, c, d;

        public void set(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public void normalize() {
            double length = sqrt(a * a + b * b + c * c);
            a /= length;
            b /= length;
            c /= length;
            d /= length;
        }

        public boolean isPointInside(float x, float y, float z) {
            return a * x + b * y + c * z + d >= 0;
        }

        public boolean isPointInside(double x, double y, double z) {
            return a * x + b * y + c * z + d >= 0;
        }
    }

    // Matrices
    static Matrix4d projection = new Matrix4d();

    // Default camera values
    private static final float YAW = 0.0f;
    private static final float PITCH = -45.0f;
    private static final float SENSITIVITY = 0.1f;
    public  static final float ZOOM = 72.0f;

    // camera Attributes
    public static Vector3d Position = new Vector3d(0.0, 150.0, 0.0);

    private static Vector3f Front = new Vector3f(0.0f, 0.0f, -1.0f);
    private static final Vector3f Up = new Vector3f();
    private static Vector3f Right = new Vector3f();
    private static final Vector3f WorldUp = new Vector3f(0.f, 1.f, 0.f);

    // euler Angles
    public static float Yaw = YAW;
    public static float Pitch = PITCH;
    public static float Roll = 0;

    // camera options

    public static float distanceFromPlayer = 5.0f;
    public static float targetZoom = ZOOM;
    public static float MouseSensitivity = SENSITIVITY;
    public static double Zoom = ZOOM;

    // returns the view matrix calculated using Euler Angles and the LookAt Matrix
    public static Matrix4d GetViewMatrix()
    {
        Vector3d playerPos = new Vector3d(World.player.position.x, World.player.position.y + 1.0, World.player.position.z);
        return new Matrix4d().lookAt(Position, playerPos, new Vector3d(WorldUp));
    }

    public static Matrix4d GetProjectionMatrix() { return projection; }

    public static void SetProjectionMatrix(int width, int height) {
        projection = new Matrix4d().identity()
                .perspective((float)Math.toRadians(Zoom),
                        (float)width / (float)Math.max(height, 1),
                        0.1f,
                        ChunkGen.CHUNK_SIZE * Client.renderDistance
                );
    }

    // processes input received from a mouse input system. Expects the offset value in both the x and y direction.
    public static void ProcessMouseMovement(float x_offset, float y_offset, boolean constrainPitch)
    {
        x_offset *= MouseSensitivity;
        y_offset *= MouseSensitivity;

        Yaw += x_offset;
        Pitch += y_offset;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch)
            Pitch = Math.clamp(Pitch, -89.0f, 89.0f);
    }

    public static void UpdateCameraPosition() {
        float yawRad = toRadians(Yaw);
        float pitchRad = toRadians(Pitch);

        double x = World.player.position.x + distanceFromPlayer * cos(pitchRad) * cos(yawRad);
        double y = World.player.position.y - distanceFromPlayer * sin(pitchRad);
        double z = World.player.position.z + distanceFromPlayer * cos(pitchRad) * sin(yawRad);

        Position = new Vector3d(x, y ,z);
    }

    // Frustum 
    public static Plane[] getFrustumPlanes() {
        Matrix4d vpMatrix = new Matrix4d();
        projection.mul(GetViewMatrix(), vpMatrix);

        Plane[] planes = new Plane[6];
        for (int i = 0; i < 6; i++) {
            planes[i] = new Plane();
        }
        
        // Récupération des 6 plans
        planes[0].set(vpMatrix.get(0,3) + vpMatrix.get(0,0), vpMatrix.get(1,3) + vpMatrix.get(1,0), vpMatrix.get(2,3) + vpMatrix.get(2,0), vpMatrix.get(3,3) + vpMatrix.get(3,0)); // Plan gauche
        planes[1].set(vpMatrix.get(0,3) - vpMatrix.get(0,0), vpMatrix.get(1,3) - vpMatrix.get(1,0), vpMatrix.get(2,3) - vpMatrix.get(2,0), vpMatrix.get(3,3) - vpMatrix.get(3,0)); // Plan droit
        planes[2].set(vpMatrix.get(0,3) + vpMatrix.get(0,1), vpMatrix.get(1,3) + vpMatrix.get(1,1), vpMatrix.get(2,3) + vpMatrix.get(2,1), vpMatrix.get(3,3) + vpMatrix.get(3,1)); // Plan bas
        planes[3].set(vpMatrix.get(0,3) - vpMatrix.get(0,1), vpMatrix.get(1,3) - vpMatrix.get(1,1), vpMatrix.get(2,3) - vpMatrix.get(2,1), vpMatrix.get(3,3) - vpMatrix.get(3,1)); // Plan haut
        planes[4].set(vpMatrix.get(0,3) + vpMatrix.get(0,2), vpMatrix.get(1,3) + vpMatrix.get(1,2), vpMatrix.get(2,3) + vpMatrix.get(2,2), vpMatrix.get(3,3) + vpMatrix.get(3,2)); // Plan proche
        planes[5].set(vpMatrix.get(0,3) - vpMatrix.get(0,2), vpMatrix.get(1,3) - vpMatrix.get(1,2), vpMatrix.get(2,3) - vpMatrix.get(2,2), vpMatrix.get(3,3) - vpMatrix.get(3,2)); // Plan éloigné

        // Normalisation des plans pour éviter les erreurs de distance
        for (Plane plane : planes) {
            plane.normalize();
        }

        return planes;
    }

    public static Vector3d getFrustumMin() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;

        for(Vector3d corner: getFrustumCorners()) {
            minX = min(minX, corner.x);
            minY = min(minY, corner.y);
            minZ = min(minZ, corner.z);
        }
        return new Vector3d(minX, minY, minZ);
    }

    public static Vector3d getFrustumMax() {
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        for(Vector3d corner: getFrustumCorners()) {
            maxX = max(maxX, corner.x);
            maxY = max(maxY, corner.y);
            maxZ = max(maxZ, corner.z);
        }

        return new Vector3d(maxX, maxY, maxZ);
    }

    private static List<Vector3d> getFrustumCorners() {
        List<Vector3d> corners = new ArrayList<>();

        Matrix4d vpMatrix = new Matrix4d();
        projection.mul(GetViewMatrix(), vpMatrix).invert();

        Vector4d[] ndcCorners = {
                new Vector4d(-1, -1, -1, 1), new Vector4d(1, -1, -1, 1),
                new Vector4d(-1, 1, -1, 1), new Vector4d(1, 1, -1, 1),
                new Vector4d(-1, -1, 1, 1), new Vector4d(1, -1, 1, 1),
                new Vector4d(-1, 1, 1, 1), new Vector4d(1, 1, 1, 1)
        };

        for(Vector4d corner : ndcCorners) {
            vpMatrix.transform(corner);
            corners.add(new Vector3d(corner.x / corner.w, corner.y / corner.w, corner.z / corner.w));
        }
        return corners;
    }

    public static double[] getFrustumData() {
        ArrayList<Double> data = new ArrayList<>();

        for(int i = 0; i < 6; i++) {
            data.add(getFrustumPlanes()[i].a);
            data.add(getFrustumPlanes()[i].b);
            data.add(getFrustumPlanes()[i].c);
            data.add(getFrustumPlanes()[i].d);
        }

        double[] result = new double[data.size()];
        for(int i = 0; i < data.size(); i++) {
            result[i] = data.get(i);
        }

        return result;
    }
    // Getters
    public static void setFront(Vector3f f) { Front = new Vector3f(f); }
    public static Vector3f getFront() { return Front; }

    public static void setRight(Vector3f r) { Right = new Vector3f(r); }
    public static Vector3f getRight() { return Right; }
    public static Vector3f getUp() { return Up; }
    public static Vector3f getWorldUp() { return WorldUp; }
}
