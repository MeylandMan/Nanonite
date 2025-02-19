package Core;

import Core.Rendering.Camera;
import org.lwjgl.glfw.*;
import static org.lwjgl.glfw.GLFW.*;

public class Input {

    public static boolean is_locked;
    public static boolean is_debug;

    public enum InputState {
        NOTHING,
        PRESSED,
        HOLDING,
        RELEASED
    }
    // Key Binding
    private static final int[] Input_bindings = {
            //UP
            GLFW_KEY_W,
            //DOWN
            GLFW_KEY_S,
            //LEFT
            GLFW_KEY_A,
            //RIGHT
            GLFW_KEY_D,
            //DEBUG
            GLFW_KEY_F3,
            //REMOVE LOCK IN
            GLFW_KEY_Q,
            //ESCAPE
            GLFW_KEY_ESCAPE,
            //JUMP
            GLFW_KEY_SPACE,
            //SNEAK
            GLFW_KEY_LEFT_SHIFT,
            //SPRINT
            GLFW_KEY_LEFT_CONTROL
    };

    // Keys
    private static final InputState[] Keys = {
            //UP
            InputState.NOTHING,
            //DOWN
            InputState.NOTHING,
            //LEFT
            InputState.NOTHING,
            //RIGHT
            InputState.NOTHING,

            //DEBUG
            InputState.NOTHING,
            //REMOVE LOCK IN
            InputState.NOTHING,
            //ESCAPE
            InputState.NOTHING,
            //Jump
            InputState.NOTHING,
            //SNEAK
            InputState.NOTHING,
            //SPRINT
            InputState.NOTHING
    };

    //Input Macros
    public static final int KEY_UP = 0;
    public static final int KEY_DOWN = 1;
    public static final int KEY_LEFT = 2;
    public static final int KEY_RIGHT = 3;

    public static final int KEY_DEBUG = 4;
    public static final int KEY_LOCK = 5;
    public static final int KEY_ESCAPE = 6;

    public static final int KEY_JUMP = 7;
    public static final int KEY_SNEAK = 8;
    public static final int KEY_SPRINT = 9;

    public void changeInputBinding(int key_binding, int key) {
        if(key_binding > Input_bindings.length) {
            System.out.println("Wrong key binding: " + key_binding);
            return;
        }

        Input_bindings[key_binding] = key;
    }

    public static boolean isKeyNotUsed(int key) {
        if(key > Input_bindings.length) {
            System.out.println("Wrong key: " + key);
            return false;
        }
        return (Keys[key] == InputState.NOTHING);
    }

    public static boolean isKeyJustPressed(int key) {
        if(key > Input_bindings.length) {
            System.out.println("Wrong key: " + key);
            return false;
        }
        return (Keys[key] == InputState.PRESSED);
    }

    public static boolean isKeyPressed(int key) {
        if(key > Input_bindings.length) {
            System.out.println("Wrong key: " + key);
            return false;
        }
        return (Keys[key] == InputState.HOLDING);
    }

    public static boolean isKeyReleased(int key) {
        if(key > Input_bindings.length) {
            System.out.println("Wrong key: " + key);
            return false;
        }
        return (Keys[key] == InputState.RELEASED);
    }

    public static boolean isMoveKeyNotUsed() {
        return (isKeyNotUsed(KEY_UP) && isKeyNotUsed(KEY_DOWN) &&
                isKeyNotUsed(KEY_LEFT) && isKeyNotUsed(KEY_RIGHT));
    }

    public static void Update(long window) {
        if (is_locked) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            if(isKeyJustPressed(KEY_DEBUG)) {
                is_debug = !is_debug;
            }
        }
        else
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

        for(int i = 0; i < Input_bindings.length; i++) {
            if (glfwGetKey(window, Input_bindings[i]) == GLFW_PRESS)
            {
                if (Keys[i] == InputState.PRESSED)
                    Keys[i] = InputState.HOLDING;
                else if (Keys[i] !=InputState.HOLDING)
                    Keys[i] = InputState.PRESSED;
            } else {
                if (Keys[i] == InputState.RELEASED)
                    Keys[i] = InputState.NOTHING;
                else if (Keys[i] !=InputState.NOTHING)
                    Keys[i] = InputState.RELEASED;
            }
        }

        if(isKeyJustPressed(KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window, true);
        }

        if(isKeyJustPressed(KEY_LOCK)) {
            is_locked = !is_locked;
        }

    }
}
