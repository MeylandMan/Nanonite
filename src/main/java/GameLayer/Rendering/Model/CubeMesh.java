package GameLayer.Rendering.Model;

import GameLayer.Rendering.*;
import org.joml.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL30.*;
public class CubeMesh {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f scale;
    private final String texture_path;

    // Datas
    public float[] vertices = {};

    public int[] indices = {};

    // OpenGL Context
    VAO m_Vao;
    public Texture texture;
    VBO m_Vbo;
    EBO m_Ebo;

    public CubeMesh() {
        this.texture_path = "dirt.png";
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);
    }

    public CubeMesh(String texture) {
        this.texture_path = texture;
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);
    }

    public CubeMesh(String texture, Vector3f position) {
        this.texture_path = texture;
        this.position = new Vector3f(position);
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);
    }

    public CubeMesh(String texture, Vector3f position, Vector3f rotation) {
        this.texture_path = texture;
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f(1.0f);
    }

    public CubeMesh(String texture, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.texture_path = texture;
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f(scale);
    }

    public void Draw() {
        texture.Bind();
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

    public void Init() {
        int vertexs = vertices.length/8;
        setupMesh();
        setupNormal();
    }
    void setupMesh() {
        if (!GL.getCapabilities().OpenGL30) {
            throw new IllegalStateException("OpenGL 3.0 non disponible !");
        }

        m_Vao = new VAO();
        m_Vbo = new VBO();
        m_Ebo = new EBO();

        VertexBufferLayout layout = new VertexBufferLayout();
        texture = new Texture(texture_path);

        // Initialize them
        m_Vbo.Init(vertices);
        layout.Add(3);
        layout.Add(2);
        layout.Add(3);
        m_Vao.AddBuffer(m_Vbo, layout);

        m_Ebo.Init(indices);


    }

    public void Add(float[] vertices, int[] indices) {
        this.vertices = vertices;
        this.indices = indices;
    }

    // Unused for now
    void DrawNormals() {

    }
    void setupNormal() {

    }
    // -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-

}
