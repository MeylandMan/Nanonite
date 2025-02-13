package GameLayer.Rendering.Model;

import GameLayer.Rendering.EBO;
import GameLayer.Rendering.VAO;
import GameLayer.Rendering.VBO;
import GameLayer.Rendering.VertexBufferLayout;

import static org.lwjgl.opengl.GL11.*;

public class SpriteMesh {

    // Datas
    float[] vertices = {};

    // OpenGL Context
    public VAO vao;
    public VBO vbo;


    public SpriteMesh(float x, float y, float width, float height) {
        this.vertices = new float[] {
                x, y-height,            0.f, 0.f,
                x,  y,                  0.f, 0.f,

                x+width,   y,           0.f, 0.f,
                x+width,  y-height,     0.f, 0.f
        };
        setupMesh();
    }
    public void Draw() {
        vao.Bind();
        glDrawArrays(GL_TRIANGLES, 0, 6);
        vao.UnBind();

    }
    public void Delete() {
        // Delete them
        vao.Delete();
        vbo.Delete();
    }


    void setupMesh() {
        // Instantiate them
        vao = new VAO();
        vbo = new VBO();
        VertexBufferLayout layout = new VertexBufferLayout();

        // Initialize them
        vbo.Init(vertices);
        layout.Add(2);
        layout.Add(2);
        vao.AddBuffer(vbo, layout);
    }
}
