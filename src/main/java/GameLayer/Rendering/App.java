package GameLayer.Rendering;

import GameLayer.Rendering.Model.CubeMesh;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import Core.Input.Input;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class App {
    private long window;
    private int m_Width;
    private int m_Height;
    private final String m_Title;

    public Renderer renderer;
    public Input input;
    CubeMesh cube;
    Shader shader = new Shader();

    public App(int width, int height, String title) {
        this.m_Width = width;
        this.m_Height = height;
        this.m_Title = title;
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Delete the buffers and shader we don't need anymore
        cube.Delete();
        shader.Clear();

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        window = glfwCreateWindow(m_Width, m_Height, m_Title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        renderer = new Renderer();
        input = new Input();

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            //input.KeyCallBack(window, key, scancode, action, mods);
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

    }

    private void loop() {

        /*
         This line is critical for LWJGL's interoperation with GLFW's
         OpenGL context, or any context that is managed externally.
         LWJGL detects the context that is current in the current thread,
         creates the GLCapabilities instance and makes the OpenGL
         bindings available for use.
        */
        GL.createCapabilities();
        shader.CreateShader("shaders/Opengl/Default.vert", "shaders/Opengl/Default.frag");

        cube = new CubeMesh();

        while ( !glfwWindowShouldClose(window) ) {
            renderer.ClearColor();

            shader.Bind();
            Matrix4f Model = new Matrix4f();
            Model = Model.translate(cube.position);
            Matrix4f view = new Matrix4f();
            view.lookAt(new Vector3f(0.f, 0.f, -1.f), new Vector3f(0.f, 0.f, 0.f), new Vector3f(0.0f, 0.0f, 1.0f));
            Matrix4f Projection = new Matrix4f();
            float fov = 72.0f;

            Projection.perspective(72.f,m_Width/m_Height, 0.01f, 100.f);

            shader.UniformMatrix4x4("u_Model", Model);
            shader.UniformMatrix4x4("u_View", new Matrix4f().identity());
            shader.UniformMatrix4x4("u_Proj", Projection);

            renderer.Draw(cube);

            glfwSwapBuffers(window); // swap the color buffers
            shader.UnBind();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

    }
}
