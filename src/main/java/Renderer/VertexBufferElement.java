package Renderer;

import static org.lwjgl.opengl.GL30.*;

public class VertexBufferElement {

    public int type;
    public int count;
    public boolean normalized;

    public static int GetSizeOfType(int type) {
        return switch (type) {
            case GL_FLOAT -> 4;
            case GL_UNSIGNED_INT -> 4;
            case GL_UNSIGNED_BYTE -> 1;
            default -> 0;
        };
    }
}
