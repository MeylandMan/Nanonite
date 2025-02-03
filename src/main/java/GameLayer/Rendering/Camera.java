package GameLayer.Rendering;

import org.joml.*;

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
    public Vector3f Front;
    public Vector3f Up;
    public Vector3f Right;
    public Vector3f WorldUp;
    // euler Angles
    public float Yaw;
    public float Pitch;
    // camera options
    public float MovementSpeed;
    public float MouseSensitivity;
    public float Zoom;

    // Camera constructor
    public Camera() {

    }
    //Camera constructor with scalar values
    public Camera() {

    }

    // returns the view matrix calculated using Euler Angles and the LookAt Matrix
    public Matrix4f GetViewMatrix()
    {
        Matrix4f mat4 = new Matrix4f();
        Vector3fc temp = new Vector3f();
        temp.add(Position, Front);

        return mat4.lookAt(Position, temp, Up);
    }

    
}
