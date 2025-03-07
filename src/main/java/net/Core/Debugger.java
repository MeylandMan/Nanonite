package net.Core;

import net.GameLayer.Camera;
import net.Core.Rendering.Scene;
import net.Core.Rendering.Text.TextRenderer;
import net.GameLayer.Chunk;
import net.GameLayer.ChunkGen;
import net.GameLayer.World;
import org.joml.Vector3f;

import java.text.DecimalFormat;

import static net.Core.Input.*;
import static org.joml.Math.*;

public class Debugger {
    private static final boolean debug = true;
    public static boolean is_debug = true;
    public static boolean is_combined;
    private static final DecimalFormat df = new DecimalFormat("#.###");
    private static final DecimalFormat iFormat = new DecimalFormat("#");

    private static Vector3f copyPosition;

    protected static float debug_timestamp = 0, actual_debug_timestamp = 0;

    public static float[] fps = new float[4];


    public static void PressedCombinaisonKey(Scene scene, Camera camera) {
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

                World.addChunksToQueue(camera, true);
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_COPY)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Logger.log(Logger.Level.DEBUG,"Copied position !");
                copyPosition = new Vector3f(camera.Position);
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_PASTE)) {
                if(copyPosition == null) {
                    Logger.log(Logger.Level.DEBUG,"You have not copied position !");
                    return;
                }

                debug_timestamp = actual_debug_timestamp = 0;
                Logger.log(Logger.Level.DEBUG,"Teleported to the copied position !");
                camera.Position = new Vector3f(copyPosition);

                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_INCREASE)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Client.renderDistance++;
                Client.renderDistance = min(Client.renderDistance, Client.MAX_RENDER_DISTANCE);
                Logger.log(Logger.Level.DEBUG,"Increased Render distance: " + Client.renderDistance);

                World.addChunksToQueue(camera, true);
                is_combined = true;
            }

            if(isDebugKeyJustPressed(DEBUG_DECREASE)) {
                debug_timestamp = actual_debug_timestamp = 0;
                Client.renderDistance--;
                Client.renderDistance = max(Client.renderDistance, Client.MIN_RENDER_DISTANCE);
                Logger.log(Logger.Level.DEBUG,"Decreased Render distance: " + Client.renderDistance);

                World.addChunksToQueue(camera, true);
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

    protected static void PressedDebugKey(long window, float delta) {
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
        for (Input.InputState debugKey : Debug_Keys) {
            if (debugKey == Input.InputState.RELEASED) {
                is_combined = false;
                break;
            }
        }
    }

    protected static void render(Camera camera, TextRenderer textRenderer) {
        if(!debug || !is_debug)
            return;

        String stateInfo = Client.name + Client.version + " " + Client.type + "\n" +
                (int)fps[0] + " fps (avg: " + (int)fps[1] + ", min: " + (int)fps[2] + ", max: " + (int)fps[3] + ")";
        textRenderer.renderText(stateInfo,10, 10, 0.3f, false);

        Vector3f chunkPosition = new Vector3f(
                floor(camera.Position.x / ChunkGen.X_DIMENSION),
                floor(camera.Position.y / ChunkGen.Z_DIMENSION),
                floor(camera.Position.z / ChunkGen.Z_DIMENSION)
        );

        Vector3f blockPosition = new Vector3f(
                floor(camera.Position.x),
                floor(camera.Position.y),
                floor(camera.Position.z)
        );

        String gameInfo = "XYZ: " + df.format(camera.Position.x) +
                " / " + df.format(camera.Position.y) +
                " / " + df.format(camera.Position.z) +
                "\nBlocks: " +
                df.format(blockPosition.x) + " " +
                df.format(blockPosition.y) + " " +
                df.format(blockPosition.z) +
                "\nChunks: " +
                df.format(chunkPosition.x) + " " +
                df.format(chunkPosition.y) + " " +
                df.format(chunkPosition.z) +
                "\nFacing Direction: " +
                df.format(camera.getFront().x) + " / " +
                df.format(camera.getFront().y) + " / " +
                df.format(camera.getFront().z) +
                "\nVelocity: " +
                df.format(camera.velocity.x) + " / " +
                df.format(camera.velocity.y) + " / " +
                df.format(camera.velocity.z) +
                "\ntarget speed: " + df.format(camera.targetSpeed) +
                "\nCurrent Speed: " + df.format(camera.currentSpeed) +
                "\nDrag Factor: " + df.format(camera.dragFactor);

        textRenderer.renderText(gameInfo,10, 150, 0.3f, false);
    }
}