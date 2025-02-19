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
            GLFW_KEY_F3
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
            InputState.NOTHING
    };

    //Input Macros
    public static final int KEY_UP = 0;
    public static final int KEY_DOWN = 1;
    public static final int KEY_LEFT = 2;
    public static final int KEY_RIGHT = 3;

    public static final int KEY_DEBUG = 4;

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

    public static void Update(long window) {

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
    }
}
