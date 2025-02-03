package GameLayer.Rendering;

import org.joml.*;
import java.lang.Math;
import java.util.Vector;

// Defines several possible options for camera movement. Used as abstraction to stay away from window-system specific input methods
enum Camera_Movement {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT
};


public class Camera {
    // Default camera values
    public static final float YAW = -90.0f;
    public static final float PITCH = 0.0f;
    public static final float SPEED = 5.f;
    public static final float SENSITIVITY = 0.1f;
    public static final float ZOOM = 45.0f;

    // camera Attributes
    public Vector3f Position;
    public Vector3f Front = new Vector3f(0.0f, 0.0f, -1.0f);
    public Vector3f Up;
    public Vector3f Right;
    public Vector3f WorldUp;
    // euler Angles
    public float Yaw;
    public float Pitch;
    // camera options
    public float MovementSpeed = SPEED;
    public float MouseSensitivity = SENSITIVITY;
    public float Zoom = ZOOM;

    // Camera constructor
    public Camera() {
        Position = new Vector3f(0.0f);
        WorldUp = new Vector3f(0.0f);
        Yaw = YAW;
        Pitch = PITCH;
        updateCameraVectors();
    }
    public Camera(Vector3f position) {
        Position = position;
        WorldUp = new Vector3f(0.0f);
        Yaw = YAW;
        Pitch = PITCH;
        updateCameraVectors();
    }
    public Camera(Vector3f position, Vector3f up) {
        Position = position;
        WorldUp = up;
        Yaw = YAW;
        Pitch = PITCH;
        updateCameraVectors();
    }
    public Camera(Vector3f position, Vector3f up, float yaw) {
        Position = position;
        WorldUp = up;
        Yaw = yaw;
        Pitch = PITCH;
        updateCameraVectors();
    }

    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        Position = position;
        WorldUp = up;
        Yaw = yaw;
        Pitch = pitch;
        updateCameraVectors();
    }
    //Camera constructor with scalar values
    public Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) {
        Position = new Vector3f(posX, posY, posZ);
        WorldUp = new Vector3f(upX, upY, upZ);
        Yaw = yaw;
        Pitch = pitch;
        updateCameraVectors();
    }

    // returns the view matrix calculated using Euler Angles and the LookAt Matrix
    public Matrix4f GetViewMatrix()
    {
        Matrix4f mat4 = new Matrix4f();
        Vector3fc temp = new Vector3f();
        temp.add(Position, Front);

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
    public void ProcessMouseMovement(float xoffset, float yoffset, boolean constrainPitch)
    {
        xoffset *= MouseSensitivity;
        yoffset *= MouseSensitivity;

        Yaw += xoffset;
        Pitch += yoffset;

        // make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch)
            Pitch = Math.clamp(Pitch, -89.0f, 89.0f);

        // update Front, Right and Up Vectors using the updated Euler angles
        updateCameraVectors();
    }

    // processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis
    public void ProcessMouseScroll(float yoffset)
    {
        Zoom -= (float)yoffset;
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
        Front = front.normalize();

        // also re-calculate the Right and Up vector
        Vector3f FWCross = new Vector3f();
        Vector3f RFCross = new Vector3f();

        FWCross.cross(Front, WorldUp);
        RFCross.cross(Right, Front);
        Right = FWCross.normalize();  // normalize the vectors, because their length gets closer to 0 the more you look up or down which results in slower movement.
        Up = RFCross.normalize();
    }
}
