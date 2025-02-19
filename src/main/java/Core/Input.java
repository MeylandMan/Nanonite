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
    

    public enum InputState {
        NOTHING,
        PRESSED,
        HOLDING,
        RELEASED
    }

    // Key Binding
    private int[] input_bindings = {
            //UP
            GLFW_KEY_W,
            //DOWN
            GLFW_KEY_S,
            //LEFT
            GLFW_KEY_A,
            //RIGHT
            GLFW_KEY_D,
            //DEBUG
            GLFW_KEY_F3
    };
    private final int bind_up = 0;
    private final int bind_down = 1;
    private final int bind_left = 2;
    private final int bind_right = 3;

    private final int debug_bind = 4;

    // Keys
    InputState key_up = InputState.NOTHING;
    InputState key_down = InputState.NOTHING;
    InputState key_left = InputState.NOTHING;
    InputState key_right = InputState.NOTHING;

    InputState key_debug = InputState.NOTHING;

    public void changeInputBinding(int key_binding, int key) {
        if(key_binding > 4) {
            System.out.println("Wrong key binding: " + key_binding);
            return;
        }

        input_bindings[key_binding] = key;
    }

    public void update(long window) {

        if (glfwGetKey(window, input_bindings[0]) == GLFW_PRESS)
        {
            if (key_up == InputState.PRESSED)
                key_up = InputState.HOLDING;
            else if (key_up !=InputState.HOLDING)
                key_up = InputState.PRESSED;
        } else {
            if (key_up == InputState.RELEASED)
                key_up = InputState.NOTHING;
            else if (key_up !=InputState.NOTHING)
                key_up = InputState.RELEASED;
        }

        if (glfwGetKey(window, input_bindings[1]) == GLFW_PRESS)
        {
            if (key_down == InputState.PRESSED)
                key_down = InputState.HOLDING;
            else if (key_down !=InputState.HOLDING)
                key_down = InputState.PRESSED;
        } else {
            if (key_down == InputState.RELEASED)
                key_down = InputState.NOTHING;
            else if (key_down !=InputState.NOTHING)
                key_down = InputState.RELEASED;
        }

        if (glfwGetKey(window, input_bindings[2]) == GLFW_PRESS)
        {
            if (key_left == InputState.PRESSED)
                key_left = InputState.HOLDING;
            else if (key_left !=InputState.HOLDING)
                key_left = InputState.PRESSED;
        } else {
            if (key_left == InputState.RELEASED)
                key_left = InputState.NOTHING;
            else if (key_left !=InputState.NOTHING)
                key_left = InputState.RELEASED;
        }

        if (glfwGetKey(window, input_bindings[3]) == GLFW_PRESS)
        {
            if (key_right == InputState.PRESSED)
                key_right = InputState.HOLDING;
            else if (key_right !=InputState.HOLDING)
                key_right = InputState.PRESSED;
        } else {
            if (key_right == InputState.RELEASED)
                key_right = InputState.NOTHING;
            else if (key_right !=InputState.NOTHING)
                key_right = InputState.RELEASED;
        }

        if (glfwGetKey(window, input_bindings[4]) == GLFW_PRESS)
        {
            if (key_debug == InputState.PRESSED)
                key_debug = InputState.HOLDING;
            else if (key_debug !=InputState.HOLDING)
                key_debug = InputState.PRESSED;
        } else {
            if (key_debug == InputState.RELEASED)
                key_debug = InputState.NOTHING;
            else if (key_debug !=InputState.NOTHING)
                key_debug = InputState.RELEASED;
        }
    }
}
