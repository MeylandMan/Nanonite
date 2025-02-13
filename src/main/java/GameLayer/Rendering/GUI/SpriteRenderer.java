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
    VAO vao;
    VBO vbo;
    Shader shader;
    Matrix4f matrix;

    public SpriteRenderer() {
        shader = new Shader();
        shader.CreateShader("shaders/Opengl/Sprite.vert", "shaders/Opengl/Sprite.frag");


        vao = new VAO();
        vbo = new VBO(GL_DYNAMIC_DRAW);
        VertexBufferLayout layout = new VertexBufferLayout();

        // Initialize them
        vbo.Init(6*4);
        layout.Add(2);
        layout.Add(2);
        vao.AddBuffer(vbo, layout);
    }

    public void getMatrixProjection(Matrix4f matrix) {
        this.matrix = matrix;
    }

    public void drawRectangle(int x, int y, int width, int height, Vector3f color, float alpha) {

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(24);
        buffer.put(new float[] {
                x,          y,          0.f, 0.f,
                x,          y + height, 0.f, 1.f,
                x + width,  y + height, 1.f, 1.f,

                x,          y,          0.f, 0.f,
                x + width,  y + height, 1.f, 1.f,
                x + width,  y,          1.f, 0.f
        }).flip();

        shader.Bind();

        vao.Bind();
        vbo.Bind();
        vbo.SubData(0, buffer);

        shader.Uniform3f("Color", color);
        shader.Uniform1f("Alpha", alpha);
        shader.UniformMatrix4x4("projection", matrix);

        glDrawArrays(GL_TRIANGLES, 0,  6);

        vao.UnBind();
        shader.UnBind();

        glDisable(GL_BLEND);
    }
}
