package net.Core;

import net.GameLayer.Camera;
import org.joml.Vector3d;
import org.joml.Vector3f;

import static net.GameLayer.Camera.*;
import static org.joml.Math.*;

public class Player extends Entity {
    // Multiplier
    public static final float FLYING_SPEED = 10;
    public static final float RUNNING_SPEED = 0.5f;

    public  static final float SPEED = 5.f;
    public static final float MAX_SPEED = 15.0f; // Vitesse max en mode spectateur 7
    public static final float ACCELERATION_FACTOR = 3.0f; // Influence de l'accélération
    public static final float DRAG_FACTOR = 10.0f; // Influence du ralentissement

    // camera options
    public float targetSpeed = SPEED;
    public float currentSpeed = SPEED;
    public float dragFactor = 1;

    private boolean UP = true;

    public Player(Vector3d Position) {
        super(Position);
    }

    public void ProcessKeyboard(Camera.Camera_Movement direction, float deltaTime) {

        UP = false;
        float pitchRad = toRadians(Pitch);
        if (direction == Camera.Camera_Movement.FORWARD) {
            velocity.x += getFront().x;
            velocity.z += getFront().z;
        }
        if (direction == Camera.Camera_Movement.BACKWARD) {
            velocity.x -= getFront().x;
            velocity.z -= getFront().z;
        }
        if (direction == Camera.Camera_Movement.LEFT) {
            velocity.x += getRight().x / cos(pitchRad);
            velocity.z += getRight().z / cos(pitchRad);
        }
        if (direction == Camera.Camera_Movement.RIGHT) {
            velocity.x -= getRight().x / cos(pitchRad);
            velocity.z -= getRight().z / cos(pitchRad);
        }
        if(direction == Camera.Camera_Movement.UP) {
            velocity.y += SPEED * 5;
            UP = true;
        }
        if(direction == Camera.Camera_Movement.DOWN) {
            velocity.y -= SPEED * 5;
            UP = true;
        }

    }

    public void updateCameraVectors(float deltaTime) {
        float yawRad = toRadians(Yaw);
        float pitchRad = toRadians(Pitch);

        Vector3f right = new Vector3f().set(
                -(cos(yawRad) * cos(pitchRad)),
                sin(pitchRad),
                -(sin(yawRad) * cos(pitchRad))
        ).normalize();

        setRight(right);

        // also re-calculate the Right and Up vector
        getFront().set(getRight()).cross(getWorldUp()).normalize();
        getUp().set(getFront()).cross(getRight()).normalize();

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

        velocity.x = (velocity.x < -1)? -1 : (velocity.x > 1)? 1 : velocity.x;
        velocity.z = (velocity.z < -1)? -1 : (velocity.z > 1)? 1 : velocity.z;

        position.add(velocity);
    }
}
