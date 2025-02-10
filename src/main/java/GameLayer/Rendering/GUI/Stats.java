package GameLayer.Rendering.GUI;

import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;


public class Stats {

    public void layout(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.malloc(stack);

            if (nk_begin(
                    ctx,
                    "Demo",
                    nk_rect(x, y, 230, 250, rect),
                    NK_WINDOW_BORDER | NK_WINDOW_MOVABLE | NK_WINDOW_SCALABLE | NK_WINDOW_MINIMIZABLE | NK_WINDOW_TITLE
            )) {
                nk_layout_row_dynamic(ctx, 10, 1);
                nk_label(ctx, "camera position: (x: 60, y: 20, z: 1000)", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 20, 1);
                nk_label(ctx, "camera direction: (x: 60, y: 20, z: 1000)", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 30, 1);
                nk_label(ctx, "fps: 400 (avg: 60, Min: 20, Max: 1000)", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 40, 1);
                nk_label(ctx, "deltaTime: 1/60", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 50, 1);
                nk_label(ctx, "vertices: 120", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 60, 1);
                nk_label(ctx, "triangles : (real: 50, rasterized: 10)", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 70, 1);
                nk_label(ctx, "drawCall : 12", NK_TEXT_LEFT);
            }
            nk_end(ctx);
        }
    }
}
