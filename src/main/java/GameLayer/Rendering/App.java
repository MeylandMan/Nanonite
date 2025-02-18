package GameLayer.Rendering;


import GameLayer.FPSMonitor;
import GameLayer.Chunk;
import GameLayer.Physics.CubeCollision;
import GameLayer.Physics.Raycast;
import GameLayer.Rendering.GUI.SpriteRenderer;
import GameLayer.Rendering.GUI.Text.Font;
import GameLayer.Rendering.GUI.Text.FontLoader;
import GameLayer.Rendering.GUI.Text.TextRenderer;
import GameLayer.Rendering.Model.SpriteMesh;
import GameLayer.World;
import org.joml.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import Core.Input.Input;


import java.lang.Math;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class App {
    private long window;
    private final String version = "0.0.0.0";
    private int m_Width = 0;
    private int m_Height = 0;
    private final String m_Title;
    boolean firstMouse = true;
    float lastX;
    float lastY;
    public Renderer renderer;
    public Input input;
    CubeCollision collision;
    Scene scene = new Scene();
    Shader shader = new Shader();
    SpriteMesh surface2D;

    Camera camera = new Camera(new Vector3f(8));
    World world;
    float delta;
    float lastFrame;

    private final int DEFAULT_WIDTH;
    private final int DEFAULT_HEIGHT;
    public App(int width, int height, String title) {
        this.m_Title = title;

        this.lastX = (float)m_Width/2;
        this.lastY = (float)m_Height/2;
        this.DEFAULT_WIDTH = width;
        this.DEFAULT_HEIGHT = height;
    }

    private void ProcessInput(long window) {
        if (Input.is_locked) {
            if(glfwGetKey(window, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
                camera.MovementSpeed = Camera.SPEED * 1.5f;
            } else {
                camera.MovementSpeed = Camera.SPEED;
            }

            if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.FORWARD, delta);
            if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.BACKWARD, delta);
            if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.LEFT, delta);
            if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.RIGHT, delta);
            if(glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.UP, delta);
            if(glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
                camera.ProcessKeyboard(Camera.Camera_Movement.DOWN, delta);
        }
    }

    public void run() {

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
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
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
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT, m_Title, NULL, NULL);
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

        GLCapabilities caps = GL.createCapabilities();

        shader.CreateShader("Chunk.comp", "Chunk.frag");

        Chunk chunk = new Chunk(scene, new Vector3f());
        /*
        for(int x = 0; x < 16; x++) {
            for(int z = 0; z < 16; z++) {
                Chunk chunk = new Chunk(scene, new Vector3f(x*16, 0, z*16));
            }
        }
        */
        //System.out.println("MAX TEXTURE YOU CAN LOAD : " + GL_MAX_TEXTURE_IMAGE_UNITS); 34930
        FPSMonitor fpsMonitor = new FPSMonitor();

        Font font;
        TextRenderer textRenderer = new TextRenderer();
        try {
            font = FontLoader.loadFont("arial.fnt");
            int textureID = FontLoader.loadTexture("arial.png", font);

            textRenderer = new TextRenderer(font);

        } catch (Exception e) {
            e.printStackTrace();
        }

        world = new World();
        world.addCollision(camera.collision);
        world.addCollision(new CubeCollision(new Vector3f(-10, 3, 0), new Vector3f(1)));

        Raycast raycast = new Raycast(camera.Position, camera.getFront());
        while ( !glfwWindowShouldClose(window) ) {
            int error;
            while ((error = glGetError()) != GL_NO_ERROR) {
                System.out.println("OpenGL Error: " + error);
            }

            fpsMonitor.update();

            float[] fps =  {
                    fpsMonitor.getFPS(),
                    fpsMonitor.getAverageFPS(),
                    fpsMonitor.getMinFPS(),
                    fpsMonitor.getMaxFPS(),
            };

            float currentFrame = (float)(glfwGetTime());
            delta = currentFrame - lastFrame;
            lastFrame = currentFrame;

            ProcessInput(window);
            if (Input.is_locked)
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            else
                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

            renderer.ClearColor();

            // Depth render
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);

            // Enable BackFace Culling
            glEnable(GL_CULL_FACE);
            glCullFace(GL_FRONT);
            glFrontFace(GL_CW);

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            shader.Bind();

            shader.UniformMatrix4x4("view", camera.GetViewMatrix());
            shader.UniformMatrix4x4("projection", camera.GetProjectionMatrix(m_Width, m_Height));

            renderer.DrawScene(scene, shader);

            raycast.origin = camera.Position;
            raycast.direction = camera.getFront();
            raycast.drawRay(10);

            world.onRender();

            // Rendering something //
            Matrix4f orthoTextMatrix = new Matrix4f().identity()
                    .ortho(0, m_Width, m_Height, 0, -1, 1);

            Matrix4f orthoSpriteMatrix = new Matrix4f().identity()
                    .ortho(0, m_Width, 0, m_Height, -1, 1);
            textRenderer.getProjectionMatrix(orthoTextMatrix);

            //spriteRenderer.getMatrixProjection(orthoSpriteMatrix);
            //spriteRenderer.drawRectangle(0, 0, 100, 100, new Vector3f(1.0f), 1);

            if(Input.is_debug) {
                textRenderer.renderText("MyCraft " + version + " Vanilla\n" +
                                (int)fps[0] + " fps (avg: " + (int)fps[1] + ", min: " + (int)fps[2] + ", max: " + (int)fps[3] + ")",
                        10, 10, 0.3f);

                textRenderer.renderText("XYZ: " + String.format("%.3f",camera.Position.x) + " / " + String.format("%.3f",camera.Position.y) + " / " + String.format("%.3f",camera.Position.z) +
                                "\nBlocks: nah\nChunks: nah\n"+
                                "Facing Direction: " + String.format("%.2f",camera.getFront().x) + " / " + String.format("%.2f",camera.getFront().y) + " / " + String.format("%.2f",camera.getFront().z),
                        10, 150, 0.3f);
            }
            glfwSwapBuffers(window);
            glfwPollEvents();

            world.onUpdate(camera, delta);
        }

    }
}
