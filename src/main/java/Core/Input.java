package Core;

import Core.Rendering.Camera;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;

public class Input extends GLFWKeyCallback {

    public static boolean is_locked;
    public static boolean is_debug;

    Camera cam;
    private static boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];

    public Input(Camera camera) {
        this.cam = camera;
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        if (key >= 0 && key < keys.length) {
            keys[key] = (action != GLFW.GLFW_RELEASE);
        }

        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
            GLFW.glfwSetWindowShouldClose(window, true);

        if (glfwGetKey(window, GLFW_KEY_Q) == GLFW_PRESS) {
            is_locked = !is_locked;
        }

        if (glfwGetKey(window, GLFW_KEY_F3) == GLFW_PRESS) {
            is_debug = !is_debug;
        }
    }

    public static boolean isKeyPressed(int key) {
        return keys[key];
    }
    public static boolean isKeyJustPressed(int key) {
        boolean _isKeyPressed = isKeyPressed(key);
        return keys[key];
    }

    public enum Mapping {
        NOTHING,
        PRESSED,
        HOLDING,
        RELEASED
    }
}
