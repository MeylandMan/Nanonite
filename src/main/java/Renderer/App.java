package Renderer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import Core.Input.Input;
import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static java.lang.Math.*;
import static Renderer.API_CONTEXT.*;

public class App {
    private long window;
    private int m_Width;
    private int m_Height;
    private final String m_Title;

    public Renderer renderer;
    public Input input;
    SpriteMesh sprite;
    Shader shader = new Shader();

    API api_context = API.OPENGL;

    public App(int width, int height, String title) {

        this.m_Width = width;
        m_Width = max(640, m_Width); // Max Width is 640

        this.m_Height = height;
        m_Height = max(480, m_Height); // Max Width is 480

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
        sprite.DeleteMesh(renderer);
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
        renderer = new Renderer(api_context);
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

        String vertexShader = "";
        String fragmentShader = "";

        if(api_context == API.OPENGL){
            vertexShader = "shaders/Opengl/Default.vert";
            fragmentShader = "shaders/Opengl/Default.frag";
        } else if(api_context == API.OPENGL_ES) {
            vertexShader = "shaders/Opengl ES/Default.vert";
            fragmentShader = "shaders/Opengl ES/Default.frag";
        }
        shader.CreateShader(api_context, vertexShader, fragmentShader);
        sprite = new SpriteMesh(renderer);

        while ( !glfwWindowShouldClose(window) ) {
            glClearColor(0.f, 0.f, 0.f, 0.f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            shader.Bind();
            sprite.Draw(renderer);

            glfwSwapBuffers(window); // swap the color buffers
            shader.UnBind();

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

    }
}
