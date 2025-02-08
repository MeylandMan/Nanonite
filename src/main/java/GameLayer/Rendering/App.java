package GameLayer.Rendering;

import GameLayer.Block;
import GameLayer.Chunk;
import GameLayer.Rendering.Model.CubeMesh;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.nuklear.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import Core.Input.Input;
import java.lang.Math;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.nuklear.Nuklear.*;

public class App {
    private long window;
    ByteBuffer container;
    private NkContext ctx;

    private int m_Width = 0;
    private int m_Height = 0;
    private final String m_Title;
    boolean firstMouse = true;
    float lastX;
    float lastY;
    public Renderer renderer;
    public Input input;
    Chunk chunk;
    Scene scene = new Scene();
    Shader shader = new Shader();
    Camera camera = new Camera(new Vector3f(0.f, 0.f, -3.f));
    float delta;
    float lastFrame;

    public App(int width, int height, String title) {
        this.m_Title = title;

        this.lastX = (float)m_Width/2;
        this.lastY = (float)m_Height/2;
    }

    private void ProcessInput(long window) {
        if (Input.is_locked) {
            if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.FORWARD, delta);
            if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.BACKWARD, delta);
            if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.LEFT, delta);
            if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.RIGHT, delta);
        }
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Delete the buffers and shader we don't need anymore
        scene.Delete();
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

        window = glfwCreateWindow(1280, 720, m_Title, NULL, NULL);

        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        renderer = new Renderer();
        input = new Input(camera);


        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            m_Width = width;
            m_Height = height;
            glViewport(0, 0, m_Width, m_Height);
        });

        glfwSetKeyCallback(window, input);
        glfwSetCursorPosCallback(window, (long window, double xposIn, double yposIn) -> {
            float xpos = (float)xposIn;
            float ypos = (float)yposIn;

            if (firstMouse)
            {
                lastX = xpos;
                lastY = ypos;
                firstMouse = false;
            }

            float xoffset = xpos - lastX;
            float yoffset = lastY - ypos; // reversed since y-coordinates go from bottom to top

            lastX = xpos;
            lastY = ypos;

            if (Input.is_locked) {
                camera.ProcessMouseMovement(xoffset, yoffset, true);
            }

        });

        glfwSetScrollCallback(window, (long window, double _, double yoffset) -> {
            camera.ProcessMouseScroll((float)yoffset);
        });


        IntBuffer pWidth;
        IntBuffer pHeight;
        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            pWidth = stack.mallocInt(1); // int*
            pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            m_Width = pWidth.get(0);
            m_Height = pHeight.get(0);
            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert(vidmode != null);
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }


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

        /*
        // Initialisation de Nuklear
        ctx = NkContext.create();
        NkUserFont defaultFont = NkUserFont.create();
        NkAllocator allocator = NkAllocator.create();
        nk_init(ctx, allocator, defaultFont);
        */

        // Depth render
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        shader.CreateShader("shaders/Opengl/Default.vert", "shaders/Opengl/Default.frag");

        Vector3f pos = new Vector3f();

        chunk = new Chunk(scene, pos);


        // Enable BackFace Culling
        glEnable(GL_CULL_FACE);

        while ( !glfwWindowShouldClose(window) ) {
            ProcessInput(window);
            if (Input.is_locked)
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            else
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

            renderer.ClearColor();

            float currentFrame = (float)(glfwGetTime());
            delta = currentFrame - lastFrame;
            lastFrame = currentFrame;

            //onGUI();

            shader.Bind();

            shader.UniformMatrix4x4("u_View", camera.GetViewMatrix());
            shader.UniformMatrix4x4("u_Proj", camera.GetProjectionMatrix(m_Width, m_Height));

            renderer.DrawScene(scene, shader);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }

    }

    private void onGUI() {
        if (nk_begin(ctx, "Hello", NkRect.malloc().set(50, 50, 300, 200),
                NK_WINDOW_BORDER|NK_WINDOW_MOVABLE)) {
            nk_layout_row_dynamic(ctx, 30, 1);
            nk_label(ctx, "Hello, Nuklear!", NK_TEXT_ALIGN_CENTERED);
        }
        nk_end(ctx);
    }
}
