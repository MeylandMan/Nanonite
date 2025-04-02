package Mycraft.Core;


import Mycraft.Debug.Debugger;
import Mycraft.Debug.FPSMonitor;
import Mycraft.Debug.Logger;
import Mycraft.Debug.MemoryManager;
import Mycraft.Rendering.*;
import GameLayer.*;
import Mycraft.Rendering.Text.*;
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
import static Mycraft.Debug.Debugger.*;

public class Application {
    private long window;
    private int m_Width = 0;
    private int m_Height = 0;
    private final String m_Title;
    public Renderer renderer;
    Scene scene = new Scene();
    World world;

    private final int DEFAULT_WIDTH;
    private final int DEFAULT_HEIGHT;

    public Application(int width, int height, String title) {
        this.m_Title = title;

        this.DEFAULT_WIDTH = width;
        this.DEFAULT_HEIGHT = height;
    }

    private void ProcessInput(double delta) {

        if (Input.is_locked) {
            if (Input.isKeyPressed(Input.KEY_SPRINT) && !Input.isKeyNotUsed(Input.KEY_UP)) {
                World.player.targetSpeed = World.player.MAX_SPEED;
                Camera.targetZoom = Camera.ZOOM*1.3f;
            } else {
                World.player.targetSpeed = World.player.SPEED;
                Camera.targetZoom = Camera.ZOOM;
            }

            if(Input.isKeyJustPressed(Input.KEY_RESET_POSITION)) {
                World.player.position = new Vector3d(8, 70, 8);
            }


            if (Input.isMoveKeyNotUsed()) {
                World.player.targetSpeed = 0;
            }
            
            if (Input.isKeyPressed(Input.KEY_UP))
                World.player.ProcessKeyboard(Camera.Camera_Movement.FORWARD);
            if (Input.isKeyPressed(Input.KEY_DOWN))
                World.player.ProcessKeyboard(Camera.Camera_Movement.BACKWARD);
            if (Input.isKeyPressed(Input.KEY_LEFT))
                World.player.ProcessKeyboard(Camera.Camera_Movement.LEFT);
            if (Input.isKeyPressed(Input.KEY_RIGHT))
                World.player.ProcessKeyboard(Camera.Camera_Movement.RIGHT);
            if(Input.isKeyPressed(Input.KEY_JUMP))
                World.player.ProcessKeyboard(Camera.Camera_Movement.UP);
            if(Input.isKeyPressed(Input.KEY_SNEAK))
                World.player.ProcessKeyboard(Camera.Camera_Movement.DOWN);
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

        GL.createCapabilities();
        Client.LoadingBlockTextures();
        Client.glVersion = glGetString(GL_VERSION);

        glEnable(GL43.GL_DEBUG_OUTPUT);
        glEnable(GL43.GL_DEBUG_OUTPUT_SYNCHRONOUS);

        //System.out.println("MAX TEXTURE YOU CAN LOAD : " + GL_MAX_TEXTURE_IMAGE_UNITS); 34930
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

        // My Debug seed: 450
        world = new World(450);
        while ( !glfwWindowShouldClose(window) ) {

            Logger.catchOpenGLErrors();
            glfwSwapInterval(Client.Vsync);

            // Updates
            FPSMonitor.update();
            MemoryManager.Update();

            double delta = FPSMonitor.getDeltaTime();

            ProcessInput(delta);
            world.onUpdate();


            Input.Update(window, delta);
            World.player.updateCameraVectors(delta);
            Camera.UpdateCameraPosition();

            long chunkX = (long) ChunkGen.getLocalChunk(World.player.position).x;
            long chunkY = (long) ChunkGen.getLocalChunk(World.player.position).y;
            long chunkZ = (long) ChunkGen.getLocalChunk(World.player.position).z;

            int X = (int) ChunkGen.getLocalBlock(Camera.Position).x;
            int Y = (int) ChunkGen.getLocalBlock(Camera.Position).y;
            int Z = (int) ChunkGen.getLocalBlock(Camera.Position).z;

            Chunk actualChunk = World.loadedChunks.get(new Vector3f((float) chunkX, (float)chunkY, (float) chunkZ));
            byte actualBlock = (actualChunk == null)? -1 : actualChunk.getBlock(X,Y,Z);

            WorldEnvironment.isUnderWater = (actualBlock == ChunkGen.BlockType.WATER.getID());

            // Rendering
            renderer.ClearColor();

            Camera.SetProjectionMatrix(m_Width, m_Height);

            //Draw chunks
            world.renderEntities();
            world.renderChunks();

            // Rendering something //
            Matrix4f orthoMatrix = new Matrix4f().identity()
                    .ortho(0, m_Width, m_Height, 0, 0.001f, 100);

            textRenderer.getProjectionMatrix(orthoMatrix);
            spriteRenderer.getMatrixProjection(orthoMatrix);
            renderer.renderInterfaces();

            Debugger.render(textRenderer, m_Width, m_Height);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }

    }
}
