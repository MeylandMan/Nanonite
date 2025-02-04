package GameLayer.Rendering.Model;

import GameLayer.Rendering.*;
import org.joml.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL30.*;
public class CubeMesh {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;
    // Datas
    float[] vertices = {
        // POSITION			    TEXTURES COORDS		Normals
        -0.5f, -0.5f, -0.5f,	0.0f, 0.0f,	        0.f, 0.f, 0.f,
         0.5f, -0.5f, -0.5f,	0.5f, 0.0f,	        0.f, 0.f, 0.f, // FRONT
         0.5f,  0.5f, -0.5f,	0.5f, 0.5f,	        0.f, 0.f, 0.f,
        -0.5f,  0.5f, -0.5f,	0.0f, 0.5f,	        0.f, 0.f, 0.f,

        -0.5f, -0.5f,  0.5f,	0.5f, 0.0f,	        0.f, 0.f, 0.f,
         0.5f, -0.5f,  0.5f,	0.0f, 0.0f,	        0.f, 0.f, 0.f, // BACK
         0.5f,  0.5f,  0.5f,	0.0f, 0.5f,	        0.f, 0.f, 0.f,
        -0.5f,  0.5f,  0.5f,	0.5f, 0.5f,	        0.f, 0.f, 0.f,

        -0.5f, -0.5f, -0.5f,	0.5f, 0.0f,	        0.f, 0.f, 0.f,
        -0.5f,  0.5f, -0.5f,	0.5f, 0.5f,	        0.f, 0.f, 0.f, // LEFT
        -0.5f,  0.5f,  0.5f,	0.0f, 0.5f,	        0.f, 0.f, 0.f,
        -0.5f, -0.5f,  0.5f,	0.0f, 0.0f,	        0.f, 0.f, 0.f,

         0.5f, -0.5f, -0.5f,	0.0f, 0.0f,	        0.f, 0.f, 0.f,
         0.5f,  0.5f, -0.5f,	0.0f, 0.5f,	        0.f, 0.f, 0.f, // RIGHT
         0.5f,  0.5f,  0.5f,	0.5f, 0.5f,	        0.f, 0.f, 0.f,
         0.5f, -0.5f,  0.5f,	0.5f, 0.0f,	        0.f, 0.f, 0.f,

        -0.5f, -0.5f, -0.5f,	0.0f, 0.0f,	        0.f, 0.f, 0.f,
         0.5f, -0.5f, -0.5f,	0.5f, 0.0f,	        0.f, 0.f, 0.f, // DOWN
         0.5f, -0.5f,  0.5f,	0.5f, 0.5f,	        0.f, 0.f, 0.f,
        -0.5f, -0.5f,  0.5f,	0.0f, 0.5f,	        0.f, 0.f, 0.f,

        0.5f,  0.5f, -0.5f,	    0.0f, 0.0f,	        0.f, 0.f, 0.f,
        -0.5f,  0.5f, -0.5f,	0.5f, 0.0f,	        0.f, 0.f, 0.f, // UP
        -0.5f,  0.5f,  0.5f,	0.5f, 0.5f,	        0.f, 0.f, 0.f,
        0.5f,  0.5f,  0.5f,	    0.0f, 0.5f,	        0.f, 0.f, 0.f,
    };
    int[] indices = {
        // front and back
        0, 3, 2,
        2, 1, 0,

        4, 5, 6,
        6, 7 ,4,
        // left and right
        11, 8, 9,
        9, 10, 11,

        12, 13, 14,
        14, 15, 12,
        // bottom and top
        16, 17, 18,
        18, 19, 16,

        20, 21, 22,
        22, 23, 20
    };

    // OpenGL Context
    VAO m_Vao;
    VBO m_Vbo;
    EBO m_Ebo;

    public CubeMesh() {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);
        setupMesh();
        setupNormal();
    }

    public CubeMesh(Vector3f position) {
        this.position = new Vector3f(position);
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);
        setupMesh();
        setupNormal();
    }

    public CubeMesh(Vector3f position, Vector3f rotation) {
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f(1.0f);
        setupMesh();
        setupNormal();
    }

    public CubeMesh(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f(scale);
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
        if (!GL.getCapabilities().OpenGL30) {
            throw new IllegalStateException("OpenGL 3.0 non disponible !");
        }

        m_Vao = new VAO();
        m_Vbo = new VBO();
        m_Ebo = new EBO();

        VertexBufferLayout layout = new VertexBufferLayout();

        // Initialize them
        m_Vbo.Init(vertices);
        layout.Add(0, 3);
        layout.Add(1, 2);
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
