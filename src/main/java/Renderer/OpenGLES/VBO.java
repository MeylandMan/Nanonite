package Renderer.OpenGLES;

import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;

import static org.lwjgl.opengles.GLES20.*;

public class VBO {
    private int m_ID;

    public void Init(float[] data) {
        m_ID = glGenBuffers();
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(data.length).put(data).flip();

        glBindBuffer(GL_ARRAY_BUFFER, m_ID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
        UnBind();
        MemoryUtil.memFree(vertexBuffer);
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
