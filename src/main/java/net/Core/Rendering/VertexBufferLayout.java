package net.Core.Rendering;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;

public class VertexBufferLayout {
    private final ArrayList<VertexBufferElement> elements;
    private int stride;

    public VertexBufferLayout() {
        this.elements = new ArrayList<>();
        this.stride = 0;
    }

    // Ajoute un élément FLOAT
    public void Add(int count) {
        elements.add(new VertexBufferElement(GL_FLOAT, count, false));
        stride += VertexBufferElement.GetSizeOfType(GL_FLOAT) * count;
    }

    public void Add(int count, int stride) {
        elements.add(new VertexBufferElement(GL_FLOAT, count, false));
        this.stride = stride;
    }

    // Add Bytes

    public void AddBytes(int count) {
        elements.add(new VertexBufferElement(GL_UNSIGNED_BYTE, count, false));
        stride += VertexBufferElement.GetSizeOfType(GL_UNSIGNED_BYTE) * count;
    }

    /* USELESS FOR NOW

    public void pushUnsignedInt(int count) {
        elements.add(new VertexBufferElement(GL_UNSIGNED_INT, count, false));
        stride += VertexBufferElement.GetSizeOfType(GL_UNSIGNED_INT) * count;
    }

    // Ajoute un élément UNSIGNED BYTE
    public void pushUnsignedByte(int count) {
        elements.add(new VertexBufferElement(GL_UNSIGNED_BYTE, count, true));
        stride += VertexBufferElement.GetSizeOfType(GL_UNSIGNED_BYTE) * count;
    }

    */


    public void clear() {
        elements.clear();
        stride = 0;
    }

    public ArrayList<VertexBufferElement> GetElements() {
        return elements;
    }

    public int GetStride() {
        return stride;
    }
}
