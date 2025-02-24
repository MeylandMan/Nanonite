package Core.Rendering;


import Core.Physics.CubeCollision;
import static GameLayer.World.*;
import static org.joml.Math.*;

import Core.Physics.Raycast;
import org.joml.Vector3f;
import org.joml.Matrix4f;

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

    // Default camera values
    private static final float YAW = 90.0f;
    private static final float PITCH = 0.0f;
    public  static final float SPEED = 5.f;
    public static final float MAX_SPEED = 7.0f; // Vitesse max en mode spectateur
    public static final float ACCELERATION_FACTOR = 3.0f; // Influence de l'accélération
    public static final float DRAG_FACTOR = 5.0f; // Influence du ralentissement
    private static final float SENSITIVITY = 0.1f;
    public  static final float ZOOM = 45.0f;

    // camera Attributes
    public CubeCollision collision;
    public Raycast raycast;
    public Vector3f Position;
    private Vector3f Front = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector3f Up = new Vector3f();
    private final Vector3f Right = new Vector3f();
    private final Vector3f WorldUp;

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

    // Camera constructor
    public Camera(Vector3f position) {
        this.Position = position;
        this.WorldUp = new Vector3f(0.f, 1.f, 0.f);
        this.Yaw = YAW;
        this.Pitch = PITCH;
        collision = new CubeCollision(new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f),
                new Vector3f(1, 2, 1));
        this.raycast = new Raycast(new Vector3f(Position.x, Position.y+1, Position.z), getFront());
        this.velocity = new Vector3f();
    }

    // returns the view matrix calculated using Euler Angles and the LookAt Matrix
    public Matrix4f GetViewMatrix()
    {
        return new Matrix4f().lookAt(Position, new Vector3f(Position).add(Front), Up);
    }

    public Matrix4f GetProjectionMatrix(int width, int height) {
        return new Matrix4f().identity()
                .perspective((float)Math.toRadians(Zoom),
                        (float)width / (float)Math.max(height, 1),
                        0.1f,
                        100.f
                );
    }

    public void ProcessKeyboard(Camera_Movement direction, float deltaTime) {

        float pitchRad = (float) Math.toRadians(Pitch);

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
            velocity.y += SPEED/currentSpeed;
        }
        if(direction == Camera_Movement.DOWN) {
            velocity.y -= SPEED/currentSpeed;
        }
        
        for(CubeCollision collision : worldCollisions) {
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
        if(targetSpeed == 0)
            currentSpeed = lerp(currentSpeed, targetSpeed, acceleration/5);
        else
            currentSpeed = lerp(currentSpeed, targetSpeed, acceleration);

        float speed = currentSpeed * deltaTime;
        velocity.mul(speed);

        Position.add(velocity);

        raycast.update(new Vector3f(Position.x, Position.y+1, Position.z), getFront());
        collision.position = new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f);
    }

    // Getters
    public Vector3f getFront() { return Front; }
    public Vector3f getRight() { return Right; }
    public Vector3f getUp() { return Up; }
}
