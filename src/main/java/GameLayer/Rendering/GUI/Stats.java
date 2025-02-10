package GameLayer.Rendering.GUI;

import GameLayer.Rendering.Camera;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;


public class Stats {

    public void layout(NkContext ctx, int x, int y, Camera camera, float[] fps) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.malloc(stack);

            if (nk_begin(
                    ctx,
                    "Demo",
                    nk_rect(x, y, 350, 350, rect),
                    NK_WINDOW_BORDER | NK_WINDOW_MOVABLE | NK_WINDOW_SCALABLE | NK_WINDOW_MINIMIZABLE | NK_WINDOW_TITLE
            )) {

                String camera_pos = "camera position: (x: " + String.format("%.2f", camera.Position.x) +
                        ", y: " + String.format("%.2f", camera.Position.y) +
                        ", z: " + String.format("%.2f", camera.Position.z) + ")";
                String view_pos = "camera position: (x: " + String.format("%.2f", camera.getFront().x) +
                        ", y: " + String.format("%.2f", camera.getFront().y) +
                        ", z: " + String.format("%.2f", camera.getFront().z) + ")";
                String fps_string_format = "";
                nk_layout_row_dynamic(ctx, 10, 1);
                nk_label(ctx, camera_pos, NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 20, 1);
                nk_label(ctx, view_pos, NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 20, 1);
                nk_label(ctx, "fps: 400 (avg: 60, Min: 20, Max: 1000)", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 20, 1);
                nk_label(ctx, "deltaTime: 1/60", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 20, 1);
                nk_label(ctx, "vertices: 120", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 20, 1);
                nk_label(ctx, "triangles : (real: 50, rasterized: 10)", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 20, 1);
                nk_label(ctx, "drawCall : 12", NK_TEXT_LEFT);
            }
            nk_end(ctx);
        }
    }
}
