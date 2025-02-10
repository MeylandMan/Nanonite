package GameLayer.Rendering.GUI;

import org.lwjgl.BufferUtils;
import org.lwjgl.nuklear.*;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.system.MemoryStack.stackPush;


class Stats {

    private static final int EASY = 0;
    private static final int HARD = 1;

    NkColorf background = NkColorf.create()
            .r(0.10f)
            .g(0.18f)
            .b(0.24f)
            .a(1.0f);

    private int op = EASY;

    private IntBuffer compression = BufferUtils.createIntBuffer(1).put(0, 20);

    Stats() {

    }

    void layout(NkContext ctx, int x, int y) {
        try (MemoryStack stack = stackPush()) {
            NkRect rect = NkRect.malloc(stack);

            if (nk_begin(
                    ctx,
                    "Demo",
                    nk_rect(x, y, 230, 250, rect),
                    NK_WINDOW_BORDER | NK_WINDOW_MOVABLE | NK_WINDOW_SCALABLE | NK_WINDOW_MINIMIZABLE | NK_WINDOW_TITLE
            )) {
                nk_layout_row_dynamic(ctx, 20, 1);
                nk_label(ctx, "fps: 400 (avg: 60, Min: 20, Max: 1000) ", NK_TEXT_LEFT);
                nk_layout_row_dynamic(ctx, 30, 1);
                nk_label(ctx, "deltaTime: 1/60", NK_TEXT_LEFT);
            }
            nk_end(ctx);
        }
    }
}
