package GameLayer.Rendering;

import GameLayer.Physics.CubeCollision;
import static GameLayer.World.*;
import org.joml.*;
import java.lang.Math;

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
    private static final float SENSITIVITY = 0.1f;
    private static final float ZOOM = 45.0f;

    // camera Attributes
    public CubeCollision collision;
    public Vector3f Position;
    private Vector3f Front = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector3f Up = new Vector3f();
    private final Vector3f Right = new Vector3f();
    private final Vector3f WorldUp;

    // euler Angles
    public float Yaw;
    public float Pitch;

    // camera options
    public float MovementSpeed = SPEED;
    public float MouseSensitivity = SENSITIVITY;
    public float Zoom = ZOOM;

    // Camera previous positions options
    private Vector3f prev_position = new Vector3f();

    // Camera constructor
    public Camera() {
        this.Position = new Vector3f();
        this.Front = new Vector3f();
        this.WorldUp = new Vector3f(0.f, 1.f, 0.f);
        this.Yaw = YAW;
        this.Pitch = PITCH;
        collision = new CubeCollision(new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f),
                new Vector3f(1, 2, 1));
        updateCameraVectors();
    }
    public Camera(Vector3f position) {
        this.Position = position;
        this.WorldUp = new Vector3f(0.f, 1.f, 0.f);
        this.Yaw = YAW;
        this.Pitch = PITCH;
        collision = new CubeCollision(new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f),
                new Vector3f(1, 2, 1));
        updateCameraVectors();
    }

    public Camera(Vector3f position, Vector3f up) {
        this. Position = position;
        this.WorldUp = new Vector3f(up);
        this.Yaw = YAW;
        this.Pitch = PITCH;
        collision = new CubeCollision(new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f),
                                      new Vector3f(1, 2, 1));
        updateCameraVectors();
    }
    public Camera(Vector3f position, Vector3f up, float yaw) {
        this.Position = position;
        this.WorldUp = new Vector3f(up);
        this.Yaw = yaw;
        this.Pitch = PITCH;
        collision = new CubeCollision(new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f),
                new Vector3f(1, 2, 1));
        updateCameraVectors();
    }

    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        this.Position = position;
        this.WorldUp = new Vector3f(up);
        this.Yaw = yaw;
        this.Pitch = pitch;
        collision = new CubeCollision(new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f),
                new Vector3f(1, 2, 1));
        updateCameraVectors();
    }
    //Camera constructor with scalar values
    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) {
        this.Position = new Vector3f(posX, posY, posZ);
        this.WorldUp = new Vector3f(upX, upY, upZ);
        this.Yaw = yaw;
        this.Pitch = pitch;
        collision = new CubeCollision(new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f),
                new Vector3f(1, 2, 1));
        updateCameraVectors();
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
    // processes input received from any keyboard-like input system. Accepts input parameter in the form of camera defined ENUM (to abstract it from windowing systems)

    void ProcessKeyboard(Camera_Movement direction, float deltaTime)
    {
        float velocity = MovementSpeed * deltaTime;
        float X = 0;
        float Y = 0;
        float Z = 0;
        float pitchRad = (float) Math.toRadians(Pitch);
        if (direction == Camera_Movement.FORWARD) {
            X += (float) ((Front.x/Math.cos(pitchRad)) * velocity);
            Z += (float) ((Front.z/Math.cos(pitchRad)) * velocity);
        }
        if (direction == Camera_Movement.BACKWARD) {
            X -= (float) ((Front.x/Math.cos(pitchRad)) * velocity);
            Z -= (float) ((Front.z/Math.cos(pitchRad)) * velocity);
        }
        if (direction == Camera_Movement.LEFT) {
            X += Right.x * velocity;
            Z += Right.z * velocity;
        }
        if (direction == Camera_Movement.RIGHT) {
            X -= Right.x * velocity;
            Z -= Right.z * velocity;
        }
        if(direction == Camera_Movement.UP) {
            Y += velocity;
        }
        if(direction == Camera_Movement.DOWN) {
            Y -= velocity;
        }

        
        for(CubeCollision collision : worldCollisions) {
            if(collision == this.collision)
                continue;

            if(collision.intersects(this.collision)) {
                X = Y = Z = 0;
                break;
            }
        }

        Position.add(X, Y, Z);
        collision.position = new Vector3f(Position.x-0.5f, Position.y-1.5f, Position.z-0.5f);

        updateCameraVectors();
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

        // update Front, Right and Up Vectors using the updated Euler angles
        updateCameraVectors();
    }

    // calculates the front vector from the Camera's (updated) Euler Angles
    public void updateCameraPrev() {
        prev_position = new Vector3f(Position);
    }

    public void updateCameraVectors()
    {
        float yawRad = (float) Math.toRadians(Yaw);
        float pitchRad = (float) Math.toRadians(Pitch);

        Front.set(
                (float) (Math.cos(yawRad) * Math.cos(pitchRad)),
                (float) Math.sin(pitchRad),
                (float) (Math.sin(yawRad) * Math.cos(pitchRad))
        ).normalize();

        // also re-calculate the Right and Up vector
        Right.set(WorldUp).cross(Front).normalize();
        Up.set(Front).cross(Right).normalize();

    }

    // Getters
    public Vector3f getFront() { return Front; }
    public Vector3f getRight() { return Right; }
    public Vector3f getUp() { return Up; }
}
