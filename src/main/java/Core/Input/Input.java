package Core.Input;

import static org.lwjgl.glfw.GLFW.*;

public class Input {

    public void KeyCallBack(long window, int key, int action, int scancode, int mods) {
        if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
            glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
    }
}
