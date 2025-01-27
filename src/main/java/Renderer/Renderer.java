package Renderer;


import Renderer.OpenGL.*;
import Renderer.OpenGLES.*;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengles.GLES20;

import static Renderer.API_CONTEXT.*;


public class Renderer {

    API m_Api;
    public API getAPI() {
        return m_Api;
    }
    public void Init(API _api) {
        this.m_Api = _api;
    }
    public void Draw() {

    }
    public void ClearColor() {

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
            case API.OPENGL_ES:
                // Instantiate them
                spriteMesh.m_ESVao = new VAO_ES();
                spriteMesh.m_ESVbo = new VBO_ES();
                spriteMesh.m_ESEbo = new EBO_ES();
                VertexBufferLayout_ES Layout = new VertexBufferLayout_ES();

                // Initialize them
                spriteMesh.m_ESVbo.Init(spriteMesh.vertices);
                Layout.Add(0, 3);
                Layout.Add(0, 3);
                spriteMesh.m_ESVao.AddBuffer(spriteMesh.m_ESVbo, Layout);

                spriteMesh.m_ESEbo.Init(spriteMesh.indices);
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
            case API.OPENGL_ES:
                // Delete them
                spriteMesh.m_ESVao.Delete();
                spriteMesh.m_ESVbo.Delete();
                spriteMesh.m_ESEbo.Delete();
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
            case API.OPENGL_ES:
                spriteMesh.m_ESVao.Bind();
                spriteMesh.m_ESEbo.Bind();

                GLES20.glDrawElements(GLES20.GL_TRIANGLES, spriteMesh.indices.length, GLES20.GL_UNSIGNED_INT, 0);

                spriteMesh.m_ESVao.UnBind();
                spriteMesh.m_ESEbo.UnBind();
                break;
            default:
                System.out.println("Error API: The current API is not supported...");
                break;

        }

    }
}
