package Core.Rendering.Text;

import Core.Rendering.Shader;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL30.*;


public class TextRenderer {
    private int vao, vbo;
    private Shader shader;
    private Font font;
    Matrix4f matrix;

    public enum TextType {
        LEFT,
        CENTER,
        RIGHT
    }

    public TextRenderer() {

    }

    public TextRenderer(Font font) {
        this.font = font;
        shader = new Shader();
        shader.CreateShader("Text.comp", "Text.frag");
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


    public void renderText(String text, float x, float y, float scale, TextType type) {
        if (type == TextType.CENTER) {
            float textWidth = getTextWidth(text, scale);
            x -= textWidth / 3.9f;
        }
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.Bind();
        float cursorX = (type == TextType.RIGHT)? x -getTextWidth(text, scale) : x;

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, font.getTextureID());

        shader.Uniform1i("fontTexture", 0);
        shader.Uniform1f("textureWidth", font.textureWidth);
        shader.Uniform1f("textureHeight", font.textureHeight);
        shader.Uniform1f("scale", scale);

        shader.UniformMatrix4x4("projection", matrix);

        for (char c : text.toCharArray()) {
            Font.CharInfo charInfo = font.getChar((int) c);
            if (c == '\n') {
                cursorX = (type == TextType.RIGHT)? x -getTextWidth(text, scale) : x;
                y += font.lineHeight * scale;
                continue;
            } else if (charInfo == null) continue;
            shader.Uniform1f("cursorX", cursorX);
            shader.Uniform1f("cursorY", y);

            shader.Uniform1f("CharX", charInfo.x);
            shader.Uniform1f("CharY", charInfo.y);
            shader.Uniform1f("CharXOffset", charInfo.xOffset);
            shader.Uniform1f("CharYOffset", charInfo.yOffset);

            shader.Uniform1f("CharWidth", charInfo.width);
            shader.Uniform1f("CharHeight", charInfo.height);

            glDrawArrays(GL_TRIANGLES, 0 ,6);
            cursorX += charInfo.xAdvance * scale;
        }
    }
}

