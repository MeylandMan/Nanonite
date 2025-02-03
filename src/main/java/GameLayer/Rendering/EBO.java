package GameLayer.Rendering;

import static org.lwjgl.opengl.GL30.*;

public class EBO {
    private int m_ID;

    public void Init(int[] data) {
        m_ID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        UnBind();
    }

    public void Delete() {
        glDeleteBuffers(m_ID);
    }

    public void Bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_ID);
    }

    public void UnBind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public int getID() {
        return m_ID;
    }
}
