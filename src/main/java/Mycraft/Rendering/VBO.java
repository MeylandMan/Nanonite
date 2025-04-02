package Mycraft.Rendering;

import static org.lwjgl.opengl.GL43.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.system.MemoryUtil;

public class VBO {
    private int  m_ID;
    private final int drawing_state;
    private final int data_type;

    public VBO() {
        this.drawing_state = GL_STATIC_DRAW;
        this.data_type = GL_ARRAY_BUFFER;
    }

    public VBO(int drawing_state) {
        this.drawing_state = drawing_state;
        this.data_type = GL_ARRAY_BUFFER;
    }

    public VBO(int drawing_state, int data_type) {
        this.drawing_state = drawing_state;
        this.data_type = data_type;
    }

    public void Init(float[] data) {
        m_ID = glGenBuffers();
        glBindBuffer(data_type, m_ID);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        glBufferData(data_type, data, drawing_state);
        UnBind();
        MemoryUtil.memFree(buffer);
    }

    public void Init(ArrayList<Byte> data) {
        m_ID = glGenBuffers();
        glBindBuffer(data_type, m_ID);

        byte[] datas = new byte[data.size()];
        for (int i = 0; i < data.size(); i++) {
            datas[i] = data.get(i);
        }

        ByteBuffer buffer = MemoryUtil.memAlloc(datas.length);
        buffer.put(datas).flip();
        glBufferData(data_type, buffer, drawing_state);
        UnBind();
        MemoryUtil.memFree(buffer);
    }

    public void Init(byte[] data) {
        m_ID = glGenBuffers();
        glBindBuffer(data_type, m_ID);

        ByteBuffer buffer = MemoryUtil.memAlloc(data.length);
        buffer.put(data).flip();
        glBufferData(data_type, buffer, drawing_state);
        UnBind();
        MemoryUtil.memFree(buffer);
    }

    public void Init(int data) {
        m_ID = glGenBuffers();
        glBindBuffer(data_type, m_ID);
        glBufferData(data_type, data, drawing_state);
        UnBind();
    }

    public void InitSSBO(int data, int binding) {
        m_ID = glGenBuffers();
        glBindBuffer(data_type, m_ID);
        glBufferData(data_type, data, drawing_state);
        glBindBufferBase(data_type, binding, m_ID);

        glBindBuffer(data_type, 0);
    }

    public void InitSSBO(FloatBuffer data, int binding) {
        m_ID = glGenBuffers();
        glBindBuffer(data_type, m_ID);
        glBufferData(data_type, data, drawing_state);
        glBindBufferBase(data_type, binding, m_ID);

        glBindBuffer(data_type, 0);
    }

    public void SubData(int offset, float[] data) {
        glBufferSubData(GL_ARRAY_BUFFER, offset, data);
    }
    public void SubData(int offset, FloatBuffer data) {
        glBufferSubData(GL_ARRAY_BUFFER, offset, data);
    }

    public void SubData(int offset, byte[] data) {
        glBindBuffer(GL_ARRAY_BUFFER, m_ID);
        glBufferSubData(GL_ARRAY_BUFFER, offset, ByteBuffer.wrap(data));
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void SubData(int offset, ByteBuffer data) {
        glBufferSubData(GL_ARRAY_BUFFER, offset, data);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void Delete() {
        glDeleteBuffers(m_ID);
    }

    public void Bind() {
        glBindBuffer(GL_ARRAY_BUFFER, m_ID);
    }

    public void BindBase(int binding) {
        glBindBufferBase(data_type, binding, m_ID);
    }

    public void UnBind() {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public int getID() {
        return m_ID;
    }
}
