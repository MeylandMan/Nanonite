package GameLayer.Rendering;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;

public class VertexBufferLayout {
    private ArrayList<VertexBufferElement> m_Elements;
    private int m_Stride;

    public VertexBufferLayout() {
        this.m_Elements = new ArrayList<VertexBufferElement>();
        this.m_Stride = 0;
    }

    public <T> void push(int count) {
        throw new UnsupportedOperationException("Template specialization is not supported in Java");
    }

    public void Add(int type, int count) {
        m_Elements.add(new VertexBufferElement());

        switch(type) {
            case 0: // Float
                m_Elements.getLast().type = GL_FLOAT;
                m_Elements.getLast().count = count;
                m_Elements.getLast().normalized = false;
                m_Stride += VertexBufferElement.GetSizeOfType(GL_FLOAT) * count;
                break;
            case 1: // Int
                m_Elements.getLast().type = GL_UNSIGNED_INT;
                m_Elements.getLast().count = count;
                m_Elements.getLast().normalized = false;
                m_Stride += VertexBufferElement.GetSizeOfType(GL_UNSIGNED_INT) * count;
                break;
            case 2: // Byte
                m_Elements.getLast().type = GL_UNSIGNED_BYTE;
                m_Elements.getLast().count = count;
                m_Elements.getLast().normalized = true;
                m_Stride += VertexBufferElement.GetSizeOfType(GL_UNSIGNED_BYTE) * count;
                break;
        }
    }

    public void Clear() {
        m_Elements.clear();
    }

    public ArrayList<VertexBufferElement> GetElements() {
        return new ArrayList<>(m_Elements);
    }

    public int GetStride() {
        return m_Stride;
    }
}
