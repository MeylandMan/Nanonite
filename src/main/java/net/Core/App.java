package net.Core;


import net.Core.Rendering.*;
import net.GameLayer.*;
import net.Core.Physics.CubeCollision;
import net.Core.Physics.Raycast;
import net.Core.Rendering.Text.*;
import org.joml.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;


import java.nio.IntBuffer;
import java.util.*;

import static org.joml.Math.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11C.glViewport;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static net.Core.Debugger.*;

public class App {
    private long window;
    private final String version = "0.0.0.0";
    private int m_Width = 0;
    private int m_Height = 0;
    private final String m_Title;
    float lastX;
    float lastY;
    public Renderer renderer;
    Scene scene = new Scene();
    Camera camera = new Camera(new Vector3d(8, 200, 8));
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
                Camera.Position = new Vector3d(8);
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
        Client.DeleteTextures();
        MultiThreading.shutdown();

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
            Logger.log(Logger.Level.ERROR, "Unable to initialize GLFW");

        // Configure GLFW
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        //glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwWindowHint(GLFW_COCOA_GRAPHICS_SWITCHING , GLFW_TRUE);

        window = glfwCreateWindow(DEFAULT_WIDTH, DEFAULT_HEIGHT, m_Title, NULL, NULL);

        if ( window == NULL )
            Logger.log(Logger.Level.ERROR, "Failed to create the GLFW window");

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
        Client.LoadingBlockTextures();

        glEnable(GL43.GL_DEBUG_OUTPUT);
        glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);
        glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            Logger.log(Logger.Level.WARNING, "GL DEBUG MESSAGE: " + glGetShaderInfoLog(id));
        }, 0);



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
        SpriteRenderer spriteRenderer = new SpriteRenderer();

        world = new World(450);
        while ( !glfwWindowShouldClose(window) ) {

            Logger.catchOpenGLErrors();
            glfwSwapInterval(Client.Vsync);
            delta = (fps[0] == 0)? 0.1f : 1/fps[0];
            ProcessInput(window);

            Vector3d chunkPos = new Vector3d(
                    floor(camera.Position.x / ChunkGen.X_DIMENSION),
                    floor(camera.Position.y / ChunkGen.Z_DIMENSION),
                    floor(camera.Position.z / ChunkGen.Z_DIMENSION)
            );

            int X = (int) floor((camera.Position.x % ChunkGen.X_DIMENSION + ChunkGen.X_DIMENSION) % ChunkGen.X_DIMENSION);
            int Y = (int) floor(camera.Position.y) + abs(ChunkGen.Y_CHUNK);
            int Z = (int) floor((camera.Position.z % ChunkGen.Z_DIMENSION + ChunkGen.Z_DIMENSION) % ChunkGen.Z_DIMENSION);

            Chunk actualChunk = World.loadedChunks.get(new Vector2f((float) chunkPos.x, (float) chunkPos.z));
            if(actualChunk != null) {
                ChunkGen.BlockType actualBlock =
                        actualChunk.blocks[X][Y][Z];

                WorldEnvironment.isUnderWater = (actualBlock == ChunkGen.BlockType.WATER);
                float fogFinalDist = (WorldEnvironment.isUnderWater)?
                        WorldEnvironment.WATER_FOG_DISTANCE : WorldEnvironment.DEFAULT_FOG_DISTANCE;


                float acc = WorldEnvironment.FOG_DISTANCE_ACCELERATION;
                float fogDist = lerp(WorldEnvironment.fogDistance, fogFinalDist, acc * delta);

                WorldEnvironment.fogDistance = fogDist;
            }


            renderer.ClearColor();

            camera.SetViewMatrix();
            camera.SetProjectionMatrix(m_Width, m_Height);

            //Draw chunks
            world.renderChunks(camera);

            //Draw entities
            //renderer.DrawScene(scene, shader);
            //world.onRender(camera.GetViewMatrix(), camera.GetProjectionMatrix(m_Width, m_Height));

            //raycast.origin = camera.Position;
            //raycast.direction = camera.getFront();
            //raycast.drawRay(10);


            // Rendering something //
            Matrix4f orthoMatrix = new Matrix4f().identity()
                    .ortho(0, m_Width, m_Height, 0, 0.001f, 100);

            textRenderer.getProjectionMatrix(orthoMatrix);
            spriteRenderer.getMatrixProjection(orthoMatrix);
            renderer.renderInterfaces();


            Debugger.render(camera, textRenderer);
            glfwSwapBuffers(window);
            glfwPollEvents();


            world.onUpdate(camera, delta);
            fpsMonitor.update();

            fps =  new float[] {
                    fpsMonitor.getFPS(),
                    fpsMonitor.getAverageFPS(),
                    fpsMonitor.getMinFPS(),
                    fpsMonitor.getMaxFPS()
            };
            Input.Update(window, camera, scene, delta);
            camera.updateCameraVectors(delta);
        }

    }
}
