package Mycraft.Debug;

import Mycraft.Core.Client;
import Mycraft.Core.Input;
import Mycraft.Rendering.Text.TextRenderer;
import Mycraft.Core.Camera;
import GameLayer.ChunkGen;
import GameLayer.World;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.text.DecimalFormat;

import static Mycraft.Core.Input.*;
import static org.joml.Math.*;
import static org.lwjgl.opengl.GL11.*;

public class Debugger {
    public static final boolean debug = true;
    public static boolean is_debug = false;
    public static boolean is_combined;
    public static boolean AmbientOcclusion = true;
    public static int Rendering = GL_FILL;

    private static final DecimalFormat df = new DecimalFormat("#.###");
    private static final DecimalFormat iFormat = new DecimalFormat("#");

    private static Vector3f copyPosition;

    public static float debug_timestamp = 0;
    protected static float actual_debug_timestamp = 0;

    public static float[] fps = new float[4];


    public static void PressedCombinaisonKey() {
        if(!debug || !is_locked)
            return;

        if(debug_timestamp != 0) {
            if(is_combined)
                return;

            if(isDebugKeyJustPressed(DEBUG_SHOW)) {
                debug_timestamp = actual_debug_timestamp = 0;
                ShowAllCommands();
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_CHUNKS)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Logger.log(Logger.Level.DEBUG,"Reload Chunks");

                World.addChunksToQueue(true);
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_COPY)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Logger.log(Logger.Level.DEBUG,"Copied position !");
                copyPosition = new Vector3f(World.player.position);
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_PASTE)) {
                if(copyPosition == null) {
                    Logger.log(Logger.Level.DEBUG,"You have not copied position !");
                    return;
                }

                debug_timestamp = actual_debug_timestamp = 0;
                Logger.log(Logger.Level.DEBUG,"Teleported to the copied position !");
                World.player.position = new Vector3d(copyPosition);

                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_INCREASE)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Client.renderDistance++;
                Client.renderDistance = min(Client.renderDistance, Client.MAX_RENDER_DISTANCE);
                Logger.log(Logger.Level.DEBUG,"Increased Render distance: " + Client.renderDistance);
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_DECREASE)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Client.renderDistance--;
                Client.renderDistance = max(Client.renderDistance, Client.MIN_RENDER_DISTANCE);
                Logger.log(Logger.Level.DEBUG,"Decreased Render distance: " + Client.renderDistance);
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_CHUNK_BORDER)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Logger.log(Logger.Level.DEBUG,"Show Chunk border");
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_PAUSE)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Logger.log(Logger.Level.DEBUG,"Paused the game");
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_VSYNC)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Client.Vsync = (Client.Vsync == 1)? 0:1;
                String message = (Client.Vsync == 1)? "Enabled Vsync" : "Disabled Vsync";
                Logger.log(Logger.Level.DEBUG, message);


                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_WIREFRAME)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Rendering = (Rendering == GL_FILL)? GL_LINE : GL_FILL;
                String message = (Rendering == GL_LINE)? "Enabled WireFrame" : "Disabled Wireframe";
                Logger.log(Logger.Level.DEBUG, message);

                is_combined = true;
            }

        }

        if(!is_combined) {
            if(actual_debug_timestamp < 0.2 && actual_debug_timestamp != 0) {
                is_debug = !is_debug;
            }
        }

        isDebugKeyAllReleased();
    }
    public static void ShowAllCommands() {
        System.out.println("""
                F3 Debug commands:
                Show all commands: F3+A
                Reload Chunks: F3+Q
                Copy Player location: F3+C
                Teleport to Copied location: F3+V
                Increase Render distance: F3+(num +)
                Decrease Render distance: F3+(num -)
                Show Chunks Borders: F3+G
                Pause the game: F3+P
                """);
    }

    public static void PressedDebugKey(long window, float delta) {
        if(!debug)
            return;

        if(isDebugKeyPressed(DEBUG_KEY)) {
            debug_timestamp += delta;
        } else {
            actual_debug_timestamp = debug_timestamp;
            debug_timestamp = 0;
        }
    }

    protected static void isDebugKeyAllReleased() {
        if(!debug)
            return;

        if(isDebugKeyJustPressed(DEBUG_AMBIENT)) {
            AmbientOcclusion = !AmbientOcclusion;
            String message = (AmbientOcclusion)? "Enabled Ambient Occlusion" : "Disabled Ambient Occlusion";
            Logger.log(Logger.Level.DEBUG, message);
        }

        for (InputState debugKey : Debug_Keys) {
            if (debugKey == Input.InputState.RELEASED) {
                is_combined = false;
                break;
            }
        }
    }

    public static void render(TextRenderer textRenderer, int width, int height) {
        if(!debug || !is_debug)
            return;

        String stateInfo = Client.name + Client.version + " " + Client.type + "\n" +
                (int)fps[0] + " fps (avg: " + (int)fps[1] + ", min: " + (int)fps[2] + ", max: " + (int)fps[3] + ")";
        textRenderer.renderText(stateInfo,10, 10, 0.25f, TextRenderer.TextType.LEFT);

        Vector3d chunkPosition = new Vector3d(
                floor(World.player.position.x / ChunkGen.CHUNK_SIZE),
                floor(World.player.position.y / ChunkGen.CHUNK_SIZE),
                floor(World.player.position.z / ChunkGen.CHUNK_SIZE)
        );

        Vector3d blockPosition = new Vector3d(
                floor((World.player.position.x % ChunkGen.CHUNK_SIZE + ChunkGen.CHUNK_SIZE) % ChunkGen.CHUNK_SIZE),
                floor((World.player.position.y % ChunkGen.CHUNK_SIZE + ChunkGen.CHUNK_SIZE) % ChunkGen.CHUNK_SIZE),
                floor((World.player.position.z % ChunkGen.CHUNK_SIZE + ChunkGen.CHUNK_SIZE) % ChunkGen.CHUNK_SIZE)
        );

        String gameInfo = "XYZ: " + df.format(World.player.position.x) +
                " / " + df.format(World.player.position.y) +
                " / " + df.format(World.player.position.z) +
                "\nBlocks: " +
                df.format(blockPosition.x) + " " +
                df.format(blockPosition.y) + " " +
                df.format(blockPosition.z) +
                "\nChunks: " +
                df.format(chunkPosition.x) + " " +
                df.format(chunkPosition.y) + " " +
                df.format(chunkPosition.z) + " (" + World.loadedChunks.size() + ", " + World.loadedChunksID.size() + ")" +
                "\nChunks draw calls: " + World.ChunkDrawCalls + " (" + World.chunksPos.size() + ")" +
                "\nFacing Direction: " +
                df.format(Camera.getFront().x) + " / " +
                df.format(Camera.getFront().y) + " / " +
                df.format(Camera.getFront().z) +
                "\nVelocity: " +
                df.format(World.player.velocity.x) + " / " +
                df.format(World.player.velocity.y) + " / " +
                df.format(World.player.velocity.z) +
                "\nRight Vector: " +
                df.format(round(Camera.getRight().x)) + " / " +
                df.format(round(Camera.getRight().y)) + " / " +
                df.format(round(Camera.getRight().z)) +
                "\ntarget speed: " + df.format(World.player.targetSpeed) +
                "\nCurrent Speed: " + df.format(World.player.currentSpeed) +
                "\nDrag Factor: " + df.format(World.player.dragFactor);

        textRenderer.renderText(gameInfo,10, 150, 0.25f, TextRenderer.TextType.LEFT);

        long FreeMemory = (Runtime.getRuntime().freeMemory() / 1024) / 1024;
        long MaxMemory = (Runtime.getRuntime().maxMemory() / 1024) / 1024;
        long TotalMemory = (Runtime.getRuntime().totalMemory() / 1024) / 1024;

        double MemPercentage = (double) FreeMemory/MaxMemory;
        double AllocPercentage = (double) TotalMemory/MaxMemory;

        String JavaVersion = "Java: " + System.getProperty("java.version");
        String UsedMem = "mem: " + df.format(MemPercentage * 100.0) + "% (" + FreeMemory + " / " + MaxMemory + " MB)";
        String AllocatedMem = "Allocated: " + df.format(AllocPercentage * 100.0) + "% " + TotalMemory + " MB";

        textRenderer.renderText(JavaVersion, width - 10, 10, 0.25f, TextRenderer.TextType.RIGHT);
        textRenderer.renderText(UsedMem, width - 10, 30, 0.25f, TextRenderer.TextType.RIGHT);
        textRenderer.renderText(AllocatedMem, width - 10, 50, 0.25f, TextRenderer.TextType.RIGHT);
        textRenderer.renderText("CPU: " + Client.processorBrand, width - 10, 100, 0.25f, TextRenderer.TextType.RIGHT);
        textRenderer.renderText("Display: " + width + "x" + height, width - 10, 150, 0.25f, TextRenderer.TextType.RIGHT);
        textRenderer.renderText("GPU: " + Client.GPUBrand, width - 10, 170, 0.25f, TextRenderer.TextType.RIGHT);
        textRenderer.renderText(Client.glVersion, width - 10, 190, 0.25f, TextRenderer.TextType.RIGHT);
    }
}