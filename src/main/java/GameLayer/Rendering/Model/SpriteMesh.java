package GameLayer.Rendering.Model;

import GameLayer.Rendering.EBO;
import GameLayer.Rendering.VAO;
import GameLayer.Rendering.VBO;
import GameLayer.Rendering.VertexBufferLayout;

import static org.lwjgl.opengl.GL11.*;

public class SpriteMesh {

    // Datas
    float[] vertices = {
            // POSITION             // COLORS
            -0.5f, -0.5f, 0.0f,  1.f, 0.f, 0.f,
            -0.5f,  0.5f, 0.0f,  0.f, 1.f, 0.f,

            0.5f,   0.5f, 0.0f,  0.f, 0.f, 1.f,
            0.5f,  -0.5f, 0.0f,  1.f, 1.f, 1.f
    };

    int[] indices = {
            1, 2, 0,
            0, 2, 3
    };

    // OpenGL Context
    VAO m_Vao;
    VBO m_Vbo;
    EBO m_Ebo;


    public SpriteMesh() {
        setupMesh();
        setupNormal();
    }
    public void Draw() {
        m_Vao.Bind();
        m_Ebo.Bind();

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        m_Vao.UnBind();
        m_Ebo.UnBind();

    }
    public void Delete() {
        // Delete them
        m_Vao.Delete();
        m_Vbo.Delete();
        m_Ebo.Delete();
    }


    void setupMesh() {
        // Instantiate them
        m_Vao = new VAO();
        m_Vbo = new VBO();
        m_Ebo = new EBO();
        VertexBufferLayout layout = new VertexBufferLayout();

        // Initialize them
        m_Vbo.Init(vertices);
        layout.Add(0, 3);
        layout.Add(0, 3);
        m_Vao.AddBuffer(m_Vbo, layout);

        m_Ebo.Init(indices);
    }

    // Unused for now
    void DrawNormals() {

    }
    void setupNormal() {

    }
    // -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
}
