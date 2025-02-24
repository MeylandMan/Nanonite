package Core;


import Core.Rendering.*;
import Core.Rendering.UI.*;
import GameLayer.FPSMonitor;
import GameLayer.Chunk;
import Core.Physics.CubeCollision;
import Core.Physics.Raycast;
import Core.Rendering.Text.*;
import GameLayer.World;
import org.joml.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;


import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

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
    float lastX;
    float lastY;
    public Renderer renderer;
    CubeCollision collision;
    Scene scene = new Scene();
    Shader shader = new Shader();

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
            if (Input.isKeyPressed(Input.KEY_SPRINT) && !Input.isKeyNotUsed(Input.KEY_UP)) {
                camera.targetSpeed = Camera.MAX_SPEED;
                camera.targetZoom = Camera.ZOOM*1.3f;
            } else {
                camera.targetSpeed = Camera.SPEED;
                camera.targetZoom = Camera.ZOOM;
            }

            if(Input.isKeyJustPressed(Input.KEY_RESET_POSITION)) {
                camera.Position = new Vector3f(8);
            }


            if (Input.isMoveKeyNotUsed()) {
                camera.targetSpeed = 0;
            }

            if (Input.isKeyPressed(Input.KEY_UP))
                camera.ProcessKeyboard(Camera.Camera_Movement.FORWARD, delta);
            if (Input.isKeyPressed(Input.KEY_DOWN))
                camera.ProcessKeyboard(Camera.Camera_Movement.BACKWARD, delta);
            if (Input.isKeyPressed(Input.KEY_LEFT))
                camera.ProcessKeyboard(Camera.Camera_Movement.LEFT, delta);
            if (Input.isKeyPressed(Input.KEY_RIGHT))
                camera.ProcessKeyboard(Camera.Camera_Movement.RIGHT, delta);
            if(Input.isKeyPressed(Input.KEY_JUMP))
                camera.ProcessKeyboard(Camera.Camera_Movement.UP, delta);
            if(Input.isKeyPressed(Input.KEY_SNEAK))
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
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        //glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindowHint(GLFW_COCOA_GRAPHICS_SWITCHING , GLFW_TRUE);

        window = glfwCreateWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT, m_Title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        renderer = new Renderer();

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            m_Width = width;
            m_Height = height;
            glViewport(0, 0, m_Width, m_Height);
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
        SpriteRenderer spriteRenderer = new SpriteRenderer();
        Font font;
        TextRenderer textRenderer = new TextRenderer();
        try {
            font = FontLoader.loadFont("arial.fnt");
            int textureID = FontLoader.loadTexture("arial.png", font);

            textRenderer = new TextRenderer(font);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // UI
        //user = new UserInterface(spriteRenderer, textRenderer);

        float value = 0;
        /*
        UISlider slider = new UISlider("Value of x: " + value,
                new Vector3f(30, 10, -10),
                new Vector2f(400, 50),
                value);
        slider.setRectangleColor(new Vector3f(0.5f));
        slider.setSliderColor(new Vector3f(1));

        UIButton button = new UIButton(
                "Press to", new Vector3f(30, 30, -10),
                new Vector2f(300, 100)
        );

        user.addElement(button);
        */



        //renderer.addInterface(user);
        world = new World();
        world.addCollision(camera.collision);
        world.addCollision(new CubeCollision(new Vector3f(4, 4, 4), new Vector3f(1)));

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

            renderer.ClearColor();

            shader.Bind();

            shader.UniformMatrix4x4("view", camera.GetViewMatrix());
            shader.UniformMatrix4x4("projection", camera.GetProjectionMatrix(m_Width, m_Height));

            renderer.DrawScene(scene, shader);

            //raycast.origin = camera.Position;
            //raycast.direction = camera.getFront();
            //raycast.drawRay(10);


            // Rendering something //
            Matrix4f orthoMatrix = new Matrix4f().identity()
                    .ortho(0, m_Width, m_Height, 0, 0.001f, 100);

            textRenderer.getProjectionMatrix(orthoMatrix);
            spriteRenderer.getMatrixProjection(orthoMatrix);
            renderer.renderInterfaces();

            if(Input.is_debug) {
                textRenderer.renderText("MyCraft " + version + " Vanilla\n" +
                                (int)fps[0] + " fps (avg: " + (int)fps[1] + ", min: " + (int)fps[2] + ", max: " + (int)fps[3] + ")",
                        10, 10, 0.3f, false);

                textRenderer.renderText("XYZ: " + String.format("%.3f",camera.Position.x) + " / " + String.format("%.3f",camera.Position.y) + " / " + String.format("%.3f",camera.Position.z) +
                                "\nBlocks: nah\nChunks: nah\n"+
                                "Facing Direction: " + String.format("%.3f",camera.getFront().x) + " / " + String.format("%.3f",camera.getFront().y) + " / " + String.format("%.3f",camera.getFront().z) +
                                "\nVelocity: " + String.format("%.3f",camera.velocity.x) + " / " + String.format("%.3f",camera.velocity.y) + " / " + String.format("%.3f",camera.velocity.z) +
                                "\ntarget speed: " + String.format("%.3f", camera.targetSpeed) + "\nCurrent Speed: " + String.format("%.3f", camera.currentSpeed),
                        10, 150, 0.3f, false);
            }


            world.onRender(camera.GetViewMatrix(), camera.GetProjectionMatrix(m_Width, m_Height));
            glfwSwapBuffers(window);
            glfwPollEvents();


            Input.Update(window, camera);
            //user.update(delta);
            camera.updateCameraVectors(delta);
            world.onUpdate(camera, delta);

        }

    }
}
