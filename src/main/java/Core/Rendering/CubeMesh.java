package Core.Rendering;

import org.joml.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL30.*;
public class CubeMesh {

    private float positionX;
    private float positionY;
    private float positionZ;

    private float rotationX;
    private float rotationY;
    private float rotationZ;

    private float scaleX;
    private float scaleY;
    private float scaleZ;

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
        this.positionX = 0.f; this.positionY = 0.f; this.positionZ = 0.f;
        this.rotationX = 0.f; this.rotationY = 0.f; this.rotationZ = 0.f;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }

    public CubeMesh(String texture) {
        this.texture_path = texture;
        this.positionX = 0.f; this.positionY = 0.f; this.positionZ = 0.f;
        this.rotationX = 0.f; this.rotationY = 0.f; this.rotationZ = 0.f;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }

    public CubeMesh(String texture, Vector3f position) {
        this.texture_path = texture;
        this.positionX = position.x; this.positionY = position.y; this.positionZ = position.z;
        this.rotationX = 0.f; this.rotationY = 0.f; this.rotationZ = 0.f;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }

    public CubeMesh(String texture, Vector3f position, Vector3f rotation) {
        this.texture_path = texture;
        this.positionX = position.x; this.positionY = position.y; this.positionZ = position.z;
        this.rotationX = rotation.x; this.rotationY = rotation.y; this.rotationZ = rotation.z;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }

    public CubeMesh(String texture, Vector3f position, Vector3f rotation, Vector3f scale) {
        this.texture_path = texture;
        this.positionX = position.x; this.positionY = position.y; this.positionZ = position.z;
        this.rotationX = rotation.x; this.rotationY = rotation.y; this.rotationZ = rotation.z;
        this.scaleX = scale.x; this.scaleY = scale.y; this.scaleZ = scale.z;
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

    public void setPosition(Vector3f position) {
        this.positionX = position.x; this.positionY = position.y; this.positionZ = position.z;
    }

    public void setRotation(Vector3f rotation) {
        this.rotationX = rotation.x; this.rotationY = rotation.y; this.rotationZ = rotation.z;
    }

    public void setScale(Vector3f scale) {
        this.scaleX = scale.x; this.scaleY = scale.y; this.scaleZ = scale.z;
    }

    public Vector3f getPosition() {
        return new Vector3f(positionX, positionY, positionZ);
    }

    public Vector3f getRotation() {
        return new Vector3f(rotationX, rotationY, rotationZ);
    }

    public Vector3f getScale() {
        return new Vector3f(scaleX, scaleY, scaleZ);
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    public float getPositionZ() {
        return positionZ;
    }

    public void setPositionZ(float positionZ) {
        this.positionZ = positionZ;
    }

    public float getRotationX() {
        return rotationX;
    }

    public void setRotationX(float rotationX) {
        this.rotationX = rotationX;
    }

    public float getRotationY() {
        return rotationY;
    }

    public void setRotationY(float rotationY) {
        this.rotationY = rotationY;
    }

    public float getRotationZ() {
        return rotationZ;
    }

    public void setRotationZ(float rotationZ) {
        this.rotationZ = rotationZ;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleZ() {
        return scaleZ;
    }

    public void setScaleZ(float scaleZ) {
        this.scaleZ = scaleZ;
    }
    // -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-

}
