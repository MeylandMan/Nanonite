package GameLayer.Rendering.GUI;

import GameLayer.Rendering.Shader;
import GameLayer.Rendering.VAO;
import GameLayer.Rendering.VBO;
import GameLayer.Rendering.VertexBufferLayout;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL30.*;

public class SpriteRenderer {
    Shader shader;
    Matrix4f matrix;

    public SpriteRenderer() {
        shader = new Shader();
        shader.CreateShader("Sprite.comp", "Sprite.frag");
    }

    public void getMatrixProjection(Matrix4f matrix) {
        this.matrix = matrix;
    }

    public void drawRectangle(int x, int y, int width, int height, Vector3f color, float alpha) {

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.Bind();
        //shader.Uniform3f("Color", color);
        //shader.Uniform1f("Alpha", alpha);
        //shader.Uniform1i("textured", 0);
        //shader.Uniform4f("transformation", x, y, width, height);
        //shader.UniformMatrix4x4("projection", matrix);
        glDrawArrays(GL_TRIANGLES, 0,  6);
        shader.UnBind();

        glDisable(GL_BLEND);
    }
}
