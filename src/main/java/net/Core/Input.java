package net.Core;

import net.GameLayer.Camera;
import net.Core.Rendering.Scene;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    //Mouse variables
    public static float mouseX = 0.0f;
    public static float mouseY = 0.0f;
    public static float lastMouseX = 0.0f;
    public static float lastMouseY = 0.0f;
    public static float mouseDeltaX = 0.0f;
    public static float mouseDeltaY = 0.0f;

    // Booleans
    public static boolean is_locked = true;

    //Datas


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
            //REMOVE LOCK IN
            GLFW_KEY_Q,
            //ESCAPE
            GLFW_KEY_ESCAPE,
            //JUMP
            GLFW_KEY_SPACE,
            //SNEAK
            GLFW_KEY_LEFT_SHIFT,
            //SPRINT
            GLFW_KEY_LEFT_CONTROL,
            //RESET POSITION
            GLFW_KEY_R
    };

    // Debug Binding
    private static final int[] Debug_bindings = {
            //DEBUG MAIN BIND
            GLFW_KEY_F3,
            //SHOW DEBUG COMB
            GLFW_KEY_A,
            //RELOAD CHUNK
            GLFW_KEY_Q,
            //Copy player location
            GLFW_KEY_C,
            //Teleport player location
            GLFW_KEY_V,
            //Increase Render distance
            GLFW_KEY_KP_ADD,
            //Decrease Render distance
            GLFW_KEY_KP_SUBTRACT,
            //Toggle chunks border
            GLFW_KEY_G,
            //Pause
            GLFW_KEY_P,
            //UNABLE V-SYNC
            GLFW_KEY_S
    };


    // Mouse Buttons
    private static final InputState[] MouseButtons = {
            InputState.NOTHING, // Left
            InputState.NOTHING, // Right
            InputState.NOTHING  // Middle
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

            //REMOVE LOCK IN
            InputState.NOTHING,
            //ESCAPE
            InputState.NOTHING,
            //Jump
            InputState.NOTHING,
            //SNEAK
            InputState.NOTHING,
            //SPRINT
            InputState.NOTHING,
            //RESET POSITION
            InputState.NOTHING
    };

    // Keys
    protected static final InputState[] Debug_Keys = {
            InputState.NOTHING,
            InputState.NOTHING,
            InputState.NOTHING,
            InputState.NOTHING,
            InputState.NOTHING,
            InputState.NOTHING,
            InputState.NOTHING,
            InputState.NOTHING,
            InputState.NOTHING,
            InputState.NOTHING
    };

    //Input Macros
    // KEYBOARD
    public static final int KEY_UP = 0;
    public static final int KEY_DOWN = 1;
    public static final int KEY_LEFT = 2;
    public static final int KEY_RIGHT = 3;

    public static final int KEY_LOCK = 4;
    public static final int KEY_ESCAPE = 5;

    public static final int KEY_JUMP = 6;
    public static final int KEY_SNEAK = 7;
    public static final int KEY_SPRINT = 8;
    public static final int KEY_RESET_POSITION = 9;

    //DEBUG
    public static final int DEBUG_KEY = 0;
    public static final int DEBUG_SHOW = 1;
    public static final int DEBUG_CHUNKS = 2;

    public static final int DEBUG_COPY = 3;
    public static final int DEBUG_PASTE = 4;
    public static final int DEBUG_INCREASE = 5;

    public static final int DEBUG_DECREASE = 6;
    public static final int DEBUG_CHUNK_BORDER = 7;
    public static final int DEBUG_PAUSE = 8;
    public static final int DEBUG_VSYNC = 9;

    //MOUSE
    public static final int MOUSE_LEFT = 0;
    public static final int MOUSE_RIGHT = 1;
    public static final int MOUSE_MIDDLE = 2;

    public void changeInputBinding(int key_binding, int key) {
        if(key_binding >= Input_bindings.length) {
            Logger.log(Logger.Level.WARNING, "Wrong key binding: " + key_binding);
            return;
        }

        Input_bindings[key_binding] = key;
    }

    public static boolean isKeyNotUsed(int key) {
        if(key >= Input_bindings.length) {
            WrongKey(key);
            return false;
        }
        return (Keys[key] == InputState.NOTHING);
    }

    public static boolean isKeyJustPressed(int key) {
        if(key >= Input_bindings.length) {
            WrongKey(key);
            return false;
        }
        return (Keys[key] == InputState.PRESSED);
    }

    public static boolean isKeyPressed(int key) {
        if(key >= Input_bindings.length) {
            WrongKey(key);
            return false;
        }
        return (Keys[key] == InputState.HOLDING);
    }

    public static boolean isKeyReleased(int key) {
        if(key >= Input_bindings.length) {
            WrongKey(key);
            return false;
        }
        return (Keys[key] == InputState.RELEASED);
    }

    public static boolean isMoveKeyNotUsed() {
        return (isKeyNotUsed(KEY_UP) && isKeyNotUsed(KEY_DOWN) &&
                isKeyNotUsed(KEY_LEFT) && isKeyNotUsed(KEY_RIGHT));
    }

    public static boolean isDebugKeyNotUsed(int key) {
        if(key >= Debug_bindings.length) {
            WrongKey(key);
            return false;
        }
        return (Debug_Keys[key] == InputState.NOTHING);
    }

    public static boolean isDebugKeyJustPressed(int key) {
        if(key >= Debug_bindings.length) {
            WrongKey(key);
            return false;
        }
        return (Debug_Keys[key] == InputState.PRESSED);
    }

    public static boolean isDebugKeyPressed(int key) {
        if(key >= Debug_bindings.length) {
            WrongKey(key);
            return false;
        }
        return (Debug_Keys[key] == InputState.HOLDING);
    }

    public static boolean isDebugKeyReleased(int key) {
        if(key >= Debug_bindings.length) {
            WrongKey(key);
            return false;
        }
        return (Debug_Keys[key] == InputState.RELEASED);
    }

    public static boolean isMouseButtonJustPressed(int button) {
        if (button >= MouseButtons.length) {
            System.out.println("Wrong mouse button: " + button);
            return false;
        }
        return (MouseButtons[button] == InputState.PRESSED);
    }

    public static boolean isMouseButtonPressed(int button) {
        if (button >= MouseButtons.length) {
            System.out.println("Wrong mouse button: " + button);
            return false;
        }
        return (MouseButtons[button] == InputState.HOLDING);
    }

    public static boolean isMouseButtonReleased(int button) {
        if (button >= MouseButtons.length) {
            System.out.println("Wrong mouse button: " + button);
            return false;
        }
        return (MouseButtons[button] == InputState.RELEASED);
    }

    public static boolean isMouseButtonNotUsed(int button) {
        if (button >= MouseButtons.length) {
            System.out.println("Wrong mouse button: " + button);
            return false;
        }
        return (MouseButtons[button] == InputState.NOTHING);
    }

    public static void setMousePosition(long window, Vector2f position) {
        glfwSetCursorPos(window, position.x, position.y);
    }

    public static void setMousePosition(long window, float x, float y) {
        glfwSetCursorPos(window, x, y);
    }

    public static void Update(long window, Camera camera, Scene scene, float deltaTime) {
        if (is_locked) {
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            //
        }

        Debugger.PressedDebugKey(window, deltaTime);
        Debugger.PressedCombinaisonKey(scene, camera);
        for(int i = 0; i < Debug_bindings.length; i++) {
            if (glfwGetKey(window, Debug_bindings[i]) == GLFW_PRESS)
            {
                if (Debug_Keys[i] == InputState.PRESSED)
                    Debug_Keys[i] = InputState.HOLDING;
                else if (Debug_Keys[i] !=InputState.HOLDING)
                    Debug_Keys[i] = InputState.PRESSED;
            } else {
                if (Debug_Keys[i] == InputState.RELEASED)
                    Debug_Keys[i] = InputState.NOTHING;
                else if (Debug_Keys[i] !=InputState.NOTHING)
                    Debug_Keys[i] = InputState.RELEASED;
            }
        }

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

        for (int i = 0; i < MouseButtons.length; i++) {
            if (glfwGetMouseButton(window, i) == GLFW_PRESS) {
                if (MouseButtons[i] == InputState.PRESSED)
                    MouseButtons[i] = InputState.HOLDING;
                else if (MouseButtons[i] != InputState.HOLDING)
                    MouseButtons[i] = InputState.PRESSED;
            } else {
                if (MouseButtons[i] == InputState.RELEASED)
                    MouseButtons[i] = InputState.NOTHING;
                else if (MouseButtons[i] != InputState.NOTHING)
                    MouseButtons[i] = InputState.RELEASED;
            }
        }

        if(isKeyJustPressed(KEY_ESCAPE)) {
            glfwSetWindowShouldClose(window, true);
        }

        if(isKeyJustPressed(KEY_LOCK) && Debugger.debug_timestamp == 0) {
            is_locked = !is_locked;
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }

        glfwSetCursorPosCallback(window, (long win, double xposIn, double yposIn) -> {
            float xpos = (float) xposIn;
            float ypos = (float) yposIn;

            // Calcul du déplacement de la souris
            mouseDeltaX = xpos - lastMouseX;
            mouseDeltaY = lastMouseY - ypos; // Inversé car l'axe Y va de bas en haut

            // Mise à jour des coordonnées
            lastMouseX = mouseX = xpos;
            lastMouseY = mouseY = ypos;

            // Si la souris est verrouillée, on transmet le mouvement à la caméra
            if (is_locked) {
                camera.ProcessMouseMovement(mouseDeltaX, mouseDeltaY, true);
            }
        });
    }

    public static void WrongKey(int key) {
        Logger.log(Logger.Level.WARNING, "Wrong Key: " + key);
    }

    public static Vector2f getMousePosition() {
        return new Vector2f(mouseX, mouseY);
    }


}
