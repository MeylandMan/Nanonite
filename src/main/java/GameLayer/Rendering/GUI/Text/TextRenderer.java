package GameLayer.Rendering.GUI.Text;

import GameLayer.Rendering.Shader;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class TextRenderer {
    private int vao, vbo;
    private Shader shader;
    private Font font;
    Matrix4f matrix;
    private int screenWidth, screenHeight;

    public TextRenderer() {

    }
    public TextRenderer(Font font) {
        this.font = font;
        shader = new Shader();
        shader.CreateShader("shaders/Opengl/Text.vert", "shaders/Opengl/Text.frag");


        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        // Position (x, y) + UV (u, v)
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void getScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void getProjectionMatrix(Matrix4f matrix) {
        this.matrix = matrix;
    }

    public void renderText(String text, float x, float y, float scale) {
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.Bind();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(text.length() * 6 * 4); // 6 vertices par quad, 4 valeurs (x, y, u, v)

        float cursorX = x;
        for (char c : text.toCharArray()) {
            Font.CharInfo charInfo = font.getChar((int) c);
            if (charInfo == null) continue; // Si caractère inconnu, on l'ignore

            float xpos = cursorX + charInfo.xOffset * scale;
            float ypos = y - charInfo.yOffset * scale;
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
                    xpos,     ypos,     u0, v0,
                    xpos,     ypos + h, u0, v1,
                    xpos + w, ypos + h, u1, v1,

                    xpos,     ypos,     u0, v0,
                    xpos + w, ypos + h, u1, v1,
                    xpos + w, ypos,     u1, v0
            });

            cursorX += charInfo.xAdvance * scale;
        }

        buffer.flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_DYNAMIC_DRAW);
        MemoryUtil.memFree(buffer);

        shader.Uniform1i("fontTexture", 0);
        shader.UniformMatrix4x4("projection", matrix);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.getTextureID());


        glDrawArrays(GL_TRIANGLES, 0, text.length() * 6);

        glBindVertexArray(0);
        shader.UnBind();

        glDisable(GL_BLEND);
    }
}

