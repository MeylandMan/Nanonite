package Renderer;

public class SpriteMesh {
    // OpenGL Context
    public VAO m_Vao;
    public VBO m_Vbo;
    public EBO m_Ebo;

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

    public SpriteMesh(Renderer renderer) {
        setupMesh(renderer);
        setupNormal();
    }
    public void Draw(Renderer renderer) {
        renderer.DrawSprite(this);
    }
    public void DeleteMesh(Renderer renderer) {
        renderer.DeleteMesh(this);
    }


    private void setupMesh(Renderer renderer) {
        renderer.InitBuffers(this);
    }

    // Unused for now
    public void DrawNormals() {

    }
    private void setupNormal() {

    }
    // -*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-
}
