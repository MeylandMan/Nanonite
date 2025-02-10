package GameLayer.Rendering.Model;

import GameLayer.Rendering.EBO;
import GameLayer.Rendering.VAO;
import GameLayer.Rendering.VBO;
import GameLayer.Rendering.VertexBufferLayout;

import static org.lwjgl.opengl.GL11.*;

public class SpriteMesh {

    // Datas
    float[] vertices = {};

    public int[] indices = {
            1, 2, 0,
            0, 2, 3
    };

    // OpenGL Context
    public VAO vao;
    public VBO vbo;
    public EBO ebo;


    public SpriteMesh() {
        this.vertices = new float[] {
                -0.5f, -0.5f, 0.0f,  1.f, 0.f, 0.f,
                -0.5f,  0.5f, 0.0f,  0.f, 1.f, 0.f,

                0.5f,   0.5f, 0.0f,  0.f, 0.f, 1.f,
                0.5f,  -0.5f, 0.0f,  1.f, 1.f, 1.f
        };
        setupMesh();
    }
    public void Draw() {
        vao.Bind();
        ebo.Bind();

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        vao.UnBind();
        ebo.UnBind();

    }
    public void Delete() {
        // Delete them
        vao.Delete();
        vbo.Delete();
        ebo.Delete();
    }


    void setupMesh() {
        // Instantiate them
        vao = new VAO();
        vbo = new VBO();
        ebo = new EBO();
        VertexBufferLayout layout = new VertexBufferLayout();

        // Initialize them
        vbo.Init(vertices);
        layout.Add(3);
        layout.Add(3);
        vao.AddBuffer(vbo, layout);

        ebo.Init(indices);
    }
}
