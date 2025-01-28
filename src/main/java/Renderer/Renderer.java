package Renderer;

import org.lwjgl.opengl.GL20;

import static Renderer.API_CONTEXT.*;


public class Renderer {

    API m_Api;

    public API getAPI() {
        return m_Api;
    }
    public Renderer(API _api) {
        this.m_Api = _api;
    }

    public void Draw() {

    }
    public void ClearColor() {
        switch (m_Api) {
            case API.OPENGL:
                GL20.glClearColor(0.f, 0.f, 0.f, 0.f);
                GL20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
                break;
            default:
                System.out.println("Error API: The current API is not supported...");
                break;
        }
    }
    public void ClearRender() {

    }
    public void onResize() {

    }
    public void InitBuffers(SpriteMesh spriteMesh) {
        switch(m_Api) {
            case API.OPENGL:
                // Instantiate them
                spriteMesh.m_Vao = new VAO();
                spriteMesh.m_Vbo = new VBO();
                spriteMesh.m_Ebo = new EBO();
                VertexBufferLayout layout = new VertexBufferLayout();

                // Initialize them
                spriteMesh.m_Vbo.Init(spriteMesh.vertices);
                layout.Add(0, 3);
                layout.Add(0, 3);
                spriteMesh.m_Vao.AddBuffer(spriteMesh.m_Vbo, layout);

                spriteMesh.m_Ebo.Init(spriteMesh.indices);

                break;
            default:
                System.out.println("Error API: The current API is not supported...");
                break;

        }


    }

    public void DeleteMesh(SpriteMesh spriteMesh) {
        switch(m_Api) {
            case API.OPENGL:
                // Delete them
                spriteMesh.m_Vao.Delete();
                spriteMesh.m_Vbo.Delete();
                spriteMesh.m_Ebo.Delete();
                break;
            default:
                System.out.println("Error API: The current API is not supported...");
                break;

        }
    }

    public void DrawSprite(SpriteMesh spriteMesh) {
        switch(m_Api) {
            case API.OPENGL:
                spriteMesh.m_Vao.Bind();
                spriteMesh.m_Ebo.Bind();

                GL20.glDrawElements(GL20.GL_TRIANGLES, spriteMesh.indices.length, GL20.GL_UNSIGNED_INT, 0);

                spriteMesh.m_Vao.UnBind();
                spriteMesh.m_Ebo.UnBind();
                break;
            default:
                System.out.println("Error API: The current API is not supported...");
                break;

        }
    }
}
