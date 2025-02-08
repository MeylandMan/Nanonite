package GameLayer.Rendering;

import static org.lwjgl.opengl.GL15.*;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

public class VBO {
    private int  m_ID;

    public void Init(float[] data) {
        m_ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, m_ID);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        UnBind();
        MemoryUtil.memFree(buffer);
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
