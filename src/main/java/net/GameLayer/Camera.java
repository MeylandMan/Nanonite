package net.GameLayer;


import net.Core.Client;
import net.Core.Physics.CubeCollision;
import static org.joml.Math.*;

import net.Core.Physics.Raycast;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Matrix4f;
import java.util.*;

// Defines several possible options for camera movement. Used as abstraction to stay away from window-system specific input methods



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
        public float a, b, c, d;

        public void set(float a, float b, float c, float d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public void normalize() {
            float length = (float) Math.sqrt(a * a + b * b + c * c);
            a /= length;
            b /= length;
            c /= length;
            d /= length;
        }

        public boolean isPointInside(float x, float y, float z) {
            return a * x + b * y + c * z + d >= 0;
        }
    }

    // Matrices
    Matrix4f projection = new Matrix4f();
    Matrix4f view = new Matrix4f();

    // Default camera values
    private static final float YAW = -90.0f;
    private static final float PITCH = 0.0f;
    public  static final float SPEED = 5.f;
    public static final float MAX_SPEED = 15.0f; // Vitesse max en mode spectateur 7
    public static final float ACCELERATION_FACTOR = 3.0f; // Influence de l'accélération
    public static final float DRAG_FACTOR = 10.0f; // Influence du ralentissement
    private static final float SENSITIVITY = 0.1f;
    public  static final float ZOOM = 45.0f;

    // Multiplier
    public static final float FLYING_SPEED = 10;
    public static final float RUNNING_SPEED = 0.5f;

    // camera Attributes
    public CubeCollision collision;
    public Raycast raycast;
    public static Vector3f Position;
    private Vector3f Front = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector3f Up = new Vector3f();
    private final Vector3f Right = new Vector3f();
    private final Vector3f WorldUp;
    private boolean UP = true;
    // euler Angles
    public float Yaw;
    public float Pitch;

    // camera options
    public Vector3f velocity;
    public float targetSpeed = SPEED;
    public float currentSpeed = SPEED;
    public float targetZoom = ZOOM;
    public float MouseSensitivity = SENSITIVITY;
    public float Zoom = ZOOM;
    public float dragFactor = 1;

    // Camera constructor
    public Camera(Vector3f position) {
        Position = position;
        this.WorldUp = new Vector3f(0.f, 1.f, 0.f);
        this.Yaw = YAW;
        this.Pitch = PITCH;
        collision = new CubeCollision(new Vector3f(), new Vector3f(1, 2, 1));
        this.raycast = new Raycast(new Vector3f(Position.x, Position.y+1, Position.z), getFront());
        this.velocity = new Vector3f();
    }

    // returns the view matrix calculated using Euler Angles and the LookAt Matrix
    public Matrix4f GetViewMatrix()
    {
        return new Matrix4f().lookAt(Position, new Vector3f(Position).add(Front), Up);
    }

    public Matrix4d GetViewMatrixd()
    {
        return new Matrix4d().lookAt(new Vector3d(Position), new Vector3d(Position).add(Front), new Vector3d(Up));
    }

    public Matrix4f GetProjectionMatrix(int width, int height) {
        return new Matrix4f().identity()
                .perspective((float)Math.toRadians(Zoom),
                        (float)width / (float)Math.max(height, 1),
                        0.1f,
                        ChunkGen.X_DIMENSION * Client.renderDistance
                );
    }

    public Matrix4d GetProjectionMatrixd(int width, int height) {
        return new Matrix4d().identity()
                .perspective((double)Math.toRadians(Zoom),
                        (double)width / (double)Math.max(height, 1),
                        0.1f,
                        ChunkGen.X_DIMENSION * Client.renderDistance
                );
    }


    public void SetViewMatrix() {
        view = new Matrix4f().lookAt(Position, new Vector3f(Position).add(Front), Up);
    }

    public void SetProjectionMatrix(int width, int height) {
        projection = new Matrix4f().identity()
                .perspective((float)Math.toRadians(Zoom),
                        (float)width / (float)Math.max(height, 1),
                        0.1f,
                        ChunkGen.X_DIMENSION * Client.renderDistance
                );
    }

    public void ProcessKeyboard(Camera_Movement direction, float deltaTime) {

        float pitchRad = (float) Math.toRadians(Pitch);
        UP = false;
        if (direction == Camera_Movement.FORWARD) {
            velocity.x += (float) (Front.x/Math.cos(pitchRad));
            velocity.z += (float) (Front.z/Math.cos(pitchRad));
        }
        if (direction == Camera_Movement.BACKWARD) {
            velocity.x -= (float) (Front.x/Math.cos(pitchRad));
            velocity.z -= (float) (Front.z/Math.cos(pitchRad));
        }
        if (direction == Camera_Movement.LEFT) {
            velocity.x += Right.x;
            velocity.z += Right.z;
        }
        if (direction == Camera_Movement.RIGHT) {
            velocity.x -= Right.x;
            velocity.z -= Right.z;
        }
        if(direction == Camera_Movement.UP) {
            velocity.y += SPEED;
            UP = true;
        }
        if(direction == Camera_Movement.DOWN) {
            velocity.y -= SPEED;
            UP = true;
        }

    }

    // processes input received from a mouse input system. Expects the offset value in both the x and y direction.
    public void ProcessMouseMovement(float x_offset, float y_offset, boolean constrainPitch)
    {
        x_offset *= MouseSensitivity;
        y_offset *= MouseSensitivity;

        Yaw += x_offset;
        Pitch += y_offset;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch)
            Pitch = Math.clamp(Pitch, -89.0f, 89.0f);
    }

    public void updateCameraVectors(float deltaTime) {
        float yawRad = toRadians(Yaw);
        float pitchRad = toRadians(Pitch);

        Front.set(
                (cos(yawRad) * cos(pitchRad)),
                sin(pitchRad),
                (sin(yawRad) * cos(pitchRad))
        ).normalize();

        // also re-calculate the Right and Up vector
        Right.set(WorldUp).cross(Front).normalize();
        Up.set(Front).cross(Right).normalize();

        float acceleration = ACCELERATION_FACTOR * deltaTime;

        Zoom = lerp(Zoom, targetZoom, acceleration*3);
        currentSpeed = lerp(currentSpeed, targetSpeed, acceleration);

            float speed = currentSpeed * deltaTime;

        if(targetSpeed == 0 && !UP) {
            velocity.x = clamp(lerp(velocity.x, 0, acceleration/dragFactor), -1, 1);
            velocity.z = clamp(lerp(velocity.z, 0, acceleration/dragFactor), -1, 1);
            dragFactor -= deltaTime * DRAG_FACTOR;
            dragFactor = max(dragFactor, 1);

        } else {
            dragFactor = DRAG_FACTOR;
            velocity.x *= speed;
            velocity.z *= speed;
            velocity.y *= deltaTime;
        }
        Position.add(velocity);

        raycast.update(new Vector3f(Position.x, Position.y+1, Position.z), getFront());
        collision.position = new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f);
    }

    // Frustum 
    public Plane[] getFrustumPlanes() {
        Matrix4f vpMatrix = new Matrix4f();
        projection.mul(view, vpMatrix);

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

    public float[] getFrustumData() {
        ArrayList<Float> data = new ArrayList<>();

        for(int i = 0; i < 6; i++) {
            data.add(getFrustumPlanes()[i].a);
            data.add(getFrustumPlanes()[i].b);
            data.add(getFrustumPlanes()[i].c);
            data.add(getFrustumPlanes()[i].d);
        }

        float[] result = new float[data.size()];
        for(int i = 0; i < data.size(); i++) {
            result[i] = data.get(i);
        }

        return result;
    }
    // Getters
    public Vector3f getFront() { return Front; }
    public Vector3f getRight() { return Right; }
    public Vector3f getUp() { return Up; }
}
