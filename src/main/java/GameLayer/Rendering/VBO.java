package GameLayer.Rendering;

import static org.lwjgl.opengl.GL15.*;
import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

public class VBO {
    private int  m_ID;
    private final int drawing_state;

    public VBO() {
        this.drawing_state = GL_STATIC_DRAW;
    }

    public VBO(int drawing_state) {
        this.drawing_state = drawing_state;
    }

    public void Init(float[] data) {
        m_ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, m_ID);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        glBufferData(GL_ARRAY_BUFFER, data, drawing_state);
        UnBind();
        MemoryUtil.memFree(buffer);
    }

    public void Init(int data) {
        m_ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, m_ID);

        glBufferData(GL_ARRAY_BUFFER, data, drawing_state);
        UnBind();
    }

    public void Init(float[] data, int drawing_state) {
        m_ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, m_ID);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        glBufferData(GL_ARRAY_BUFFER, data, drawing_state);
        UnBind();
        MemoryUtil.memFree(buffer);
    }

    public void Init(float data, int drawing_state) {
        m_ID = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, m_ID);
        FloatBuffer buffer = MemoryUtil.memAllocFloat(1);
        buffer.put(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, drawing_state);
        UnBind();
    }

    public void SubData(int offset, float[] data) {
        glBufferSubData(GL_ARRAY_BUFFER, offset, data);
    }
    public void SubData(int offset, FloatBuffer data) {
        glBufferSubData(GL_ARRAY_BUFFER, offset, data);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
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
