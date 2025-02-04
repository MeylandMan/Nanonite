package GameLayer.Rendering;

import org.joml.*;
import java.lang.Math;

// Defines several possible options for camera movement. Used as abstraction to stay away from window-system specific input methods



public class Camera {
    public enum Camera_Movement {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }

    // Default camera values
    public static final float YAW = -90.0f;
    public static final float PITCH = 0.0f;
    public static final float SPEED = 5.f;
    public static final float SENSITIVITY = 0.1f;
    public static final float ZOOM = 45.0f;

    // camera Attributes
    public Vector3f Position;
    public Vector3f Front = new Vector3f(0.0f, 0.0f, -1.0f);
    private Vector3f Up = new Vector3f();
    private Vector3f Right = new Vector3f();
    private final Vector3f WorldUp;
    // euler Angles
    public float Yaw;
    public float Pitch;
    // camera options
    public float MovementSpeed = SPEED;
    public float MouseSensitivity = SENSITIVITY;
    public float Zoom = ZOOM;

    // Camera constructor
    public Camera() {
        this.Position = new Vector3f();
        this.WorldUp = new Vector3f(0.f, 1.f, 0.f);
        this.Yaw = YAW;
        this.Pitch = PITCH;
        updateCameraVectors();
    }
    public Camera(Vector3f position) {
        this.Position = position;
        this.WorldUp = new Vector3f(0.f, 1.f, 0.f);
        this.Yaw = YAW;
        this.Pitch = PITCH;
        updateCameraVectors();
    }

    public Camera(Vector3f position, Vector3f up) {
        this. Position = position;
        this.WorldUp = up;
        this.Yaw = YAW;
        this.Pitch = PITCH;
        updateCameraVectors();
    }
    public Camera(Vector3f position, Vector3f up, float yaw) {
        this.Position = position;
        this.WorldUp = up;
        this.Yaw = yaw;
        this.Pitch = PITCH;
        updateCameraVectors();
    }

    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        this.Position = position;
        this.WorldUp = up;
        this.Yaw = yaw;
        this.Pitch = pitch;
        updateCameraVectors();
    }
    //Camera constructor with scalar values
    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) {
        this.Position = new Vector3f(posX, posY, posZ);
        this.WorldUp = new Vector3f(upX, upY, upZ);
        this.Yaw = yaw;
        this.Pitch = pitch;
        updateCameraVectors();
    }

    // returns the view matrix calculated using Euler Angles and the LookAt Matrix
    public Matrix4f GetViewMatrix()
    {
        Matrix4f mat4 = new Matrix4f();
        Vector3f temp = new Vector3f().add(Position, Front);
        //System.out.println("Front : " + Front + "\nPosition : " + Position + "\nResult : " + temp);
        return mat4.lookAt(Position, temp, Up);
    }

    // processes input received from any keyboard-like input system. Accepts input parameter in the form of camera defined ENUM (to abstract it from windowing systems)
    public void ProcessKeyboard(Camera_Movement direction, float deltaTime)
    {
        float velocity = MovementSpeed * deltaTime;
        Vector3fc[] temp = {
                new Vector3f(),
                new Vector3f()
        };

        temp[0].mul(velocity,Front);
        temp[1].mul(velocity,Right);
        switch(direction) {
            case Camera_Movement.FORWARD:
                Position = temp[0].add(temp[0], Position);
                break;
            case Camera_Movement.BACKWARD:
                Position = temp[0].sub(temp[0], Position);
                break;
            case Camera_Movement.LEFT:
                Position = temp[1].sub(temp[1], Position);
                break;
            case Camera_Movement.RIGHT:
                Position = temp[1].add(temp[1], Position);
                break;
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

        // update Front, Right and Up Vectors using the updated Euler angles
        updateCameraVectors();
    }


    public void ProcessMouseScroll(float y_offset)
    {
        Zoom -= y_offset;
        Zoom = Math.clamp(Zoom, 1.0f, 45.0f);
    }

    // calculates the front vector from the Camera's (updated) Euler Angles
    private void updateCameraVectors()
    {
        // calculate the new Front vector
        Vector3f front = new Vector3f();
        front.x = (float)(Math.cos(Math.toRadians(Yaw)) * Math.cos(Math.toRadians(Pitch)));
        front.y = (float)Math.sin(Math.toRadians(Pitch));
        front.z = (float)(Math.sin(Math.toRadians(Yaw)) * Math.cos(Math.toRadians(Pitch)));
        front.normalize(Front);

        // also re-calculate the Right and Up vector
        //Right = Front.cross(WorldUp).normalize();
        Right.cross(Right, Front).normalize();
        Up.cross(Right, Front).normalize();

        int x = 0;
    }
}
