package Core.Rendering.Text;

import Core.Rendering.Shader;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL30.*;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class TextRenderer {
    private int vao, vbo;
    private Shader shader;
    private Font font;
    Matrix4f matrix;

    public TextRenderer() {

    }

    public TextRenderer(Font font) {
        this.font = font;
        shader = new Shader();
        shader.CreateShader("Text.vert", "Text.frag");

        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 6 * 4 * 5000, GL_DYNAMIC_DRAW);

        // Position (x, y) + UV (u, v)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, 8 * Float.BYTES, 5 * Float.BYTES);
        glEnableVertexAttribArray(2);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void getProjectionMatrix(Matrix4f matrix) {
        this.matrix = matrix;
    }

    public Font getFont() {
        return font;
    }
    public float getTextWidth(String text, float scale) {
        float width = 0;
        for (char c : text.toCharArray()) {
            Font.CharInfo charInfo = font.getChar((int) c);
            if (charInfo != null) {
                width += charInfo.xAdvance * scale;
            }
        }
        return width;
    }


    public void renderText(String text, float x, float y, float scale, boolean centered) {
        if (centered) {
            float textWidth = getTextWidth(text, scale);
            x -= textWidth / 2.5f; // Décale le texte vers la gauche pour qu'il soit centré
        }

        glDisable(GL_BLEND);
        glDisable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        shader.Bind();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(text.length() * 6 * 8); // 6 vertices par quad, 4 valeurs (x, y, u, v)

        float cursorX = x;

        for (char c : text.toCharArray()) {
            Font.CharInfo charInfo = font.getChar((int) c);
            if (c == '\n') {
                cursorX = x;
                y += font.lineHeight * scale;
                continue;
            } else if (charInfo == null) continue;

            float xpos = cursorX + charInfo.xOffset * scale;
            float ypos = y + charInfo.yOffset * scale;
            float w = charInfo.width * scale;
            float h = charInfo.height * scale;

            float u0 = charInfo.x / font.textureWidth;
            float v0 = charInfo.y / font.textureHeight;
            float u1 = (charInfo.x + charInfo.width) / font.textureWidth;
            float v1 = (charInfo.y + charInfo.height) / font.textureHeight;

            // Inverse Y coordinates
            v0 = 1.0f - v0;
            v1 = 1.0f - v1;

            buffer.put(new float[]{
                    xpos,     ypos,     -1,      u0, v0, 1, 1, 1,
                    xpos,     ypos + h, -1,      u0, v1, 1, 1, 1,
                    xpos + w, ypos + h, -1,      u1, v1, 1, 1, 1,

                    xpos,     ypos,     -1,      u0, v0, 1, 1, 1,
                    xpos + w, ypos + h, -1,      u1, v1, 1, 1, 1,
                    xpos + w, ypos,     -1,      u1, v0, 1, 1, 1
            });

            cursorX += charInfo.xAdvance * scale;
        }

        buffer.flip();
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        MemoryUtil.memFree(buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        shader.Uniform1i("fontTexture", 0);
        shader.UniformMatrix4x4("projection", matrix);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.getTextureID());


        glDrawArrays(GL_TRIANGLES, 0, text.length() * 6);

        glBindVertexArray(0);

        glDisable(GL_DEPTH_TEST);
    }
}

