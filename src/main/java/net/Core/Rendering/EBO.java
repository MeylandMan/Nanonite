package net.Core.Rendering;

import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;

public class EBO {
    private int m_ID;
    private final int drawing_state;

    public EBO() {
        this.drawing_state = GL_STATIC_DRAW;
    }

    public EBO(int drawing_state) {
        this.drawing_state = drawing_state;
    }

    public void Init(int[] data) {

        m_ID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, drawing_state);
        UnBind();
    }

    public void Init(ArrayList<Integer> data) {
        m_ID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, m_ID);

        int[] datas = new int[data.size()];
        for (int i = 0; i < data.size(); i++) {
            datas[i] = data.get(i);
        }

        IntBuffer buffer = MemoryUtil.memAllocInt(datas.length);
        buffer.put(datas).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, drawing_state);
        UnBind();
        MemoryUtil.memFree(buffer);
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
