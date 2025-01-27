package Renderer.OpenGLES;

import Renderer.VertexBufferElement;
import java.util.ArrayList;

import static org.lwjgl.opengles.GLES30.*;

public class VAO_ES {
    private int m_ID;

    public VAO_ES() {
        m_ID = glGenVertexArrays();
    }

    public void Delete() {
        glDeleteVertexArrays(m_ID);
    }

    public void AddBuffer(VBO_ES vbo, VertexBufferLayout_ES layout) {

        Bind();
        vbo.Bind();
        ArrayList<VertexBufferElement> elements = layout.GetElements();

        int offset = 0;
        for (int i = 0; i < elements.size(); i++) {

            VertexBufferElement element = elements.get(i);
            glEnableVertexAttribArray(i);
            glVertexAttribPointer(i, element.count, element.type, element.normalized, layout.GetStride(), offset);

            offset += element.count * VertexBufferElement.GetSizeOfType(element.type);
        }
        UnBind();
    }

    public void Bind() {
        glBindVertexArray(m_ID);
    }

    public void UnBind() {
        glBindVertexArray(0);
    }

    public int getID() {
        return m_ID;
    }
}