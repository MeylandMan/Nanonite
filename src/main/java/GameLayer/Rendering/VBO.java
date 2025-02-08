package GameLayer.Rendering;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

public class VBO {
    private int  m_ID;

    public void Init(float[] data) {
        m_ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, m_ID);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        UnBind();
    }

    public void Delete() {
        glDeleteBuffers(m_ID);
    }

    public void Bind() {
        glBindBuffer(GL_ARRAY_BUFFER, m_ID);
    }

    public void UnBind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int getID() {
        return m_ID;
    }
}
