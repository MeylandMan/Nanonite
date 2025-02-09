package GameLayer;

import GameLayer.Rendering.Model.CubeMesh;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Block extends _Object {
    public enum Faces {
        FRONT,
        BACK,
        RIGHT,
        LEFT,
        TOP,
        BOTTOM
    }

    float[] vertices = {};
    int[] indices = {};

    BlockType type;
    public enum BlockType {
        AIR,
        DIRT
    }

    public Block() {
        super();
    }
    public Block(String texture) {
        super(texture);
    }
    public Block(String texture, Vector3f position) {
        super(texture, position);
    }

    public Block(String texture, Vector3f position, Vector3f rotation) {
        super(texture, position, rotation);
    }

    public Block(String texture, Vector3f position, Vector3f rotation, Vector3f scale) {
        super(texture, position, rotation, scale);
    }

    public void createDatas() {
        mesh.Add(this.vertices, this.indices);
        vertices = null;
        indices = null;
    }
    public void addDataToVertice(Faces face) {
        ArrayList<Float> vertex = new ArrayList<>();
        ArrayList<Integer> _indices = new ArrayList<>();
        switch (face) {
            case Faces.FRONT:
                vertex.add(-0.5f); vertex.add(-0.5f); vertex.add(-0.5f);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(-0.5f); vertex.add(-0.5f);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(0.5f); vertex.add(-0.5f);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(-0.5f); vertex.add(0.5f); vertex.add(-0.5f);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                _indices.add(0); _indices.add(3); _indices.add(2);
                _indices.add(2); _indices.add(1); _indices.add(0);
                break;
            case Faces.BACK:
                vertex.add(-0.5f); vertex.add(-0.5f); vertex.add(0.5f);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(-0.5f); vertex.add(0.5f);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(0.5f); vertex.add(0.5f);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(-0.5f); vertex.add(0.5f); vertex.add(0.5f);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                _indices.add(0); _indices.add(1); _indices.add(2);
                _indices.add(2); _indices.add(3); _indices.add(0);
                break;
            case Faces.RIGHT:
                vertex.add(-0.5f); vertex.add(-0.5f); vertex.add(-0.5f);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(-0.5f); vertex.add(0.5f); vertex.add(-0.5f);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(-0.5f); vertex.add(0.5f); vertex.add(0.5f);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(-0.5f); vertex.add(-0.5f); vertex.add(0.5f);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                _indices.add(1); _indices.add(0); _indices.add(3);
                _indices.add(1); _indices.add(3); _indices.add(2);
                break;
            case Faces.LEFT:
                vertex.add(0.5f); vertex.add(-0.5f); vertex.add(-0.5f);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(0.5f); vertex.add(-0.5f);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(0.5f); vertex.add(0.5f);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(-0.5f); vertex.add(0.5f);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                _indices.add(0); _indices.add(1); _indices.add(2);
                _indices.add(2); _indices.add(3); _indices.add(0);
                break;
            case Faces.BOTTOM:
                vertex.add(-0.5f); vertex.add(-0.5f); vertex.add(-0.5f);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(-0.5f); vertex.add(-0.5f);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(-0.5f); vertex.add(0.5f);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(-0.5f); vertex.add(-0.5f); vertex.add(0.5f);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                _indices.add(0); _indices.add(1); _indices.add(2);
                _indices.add(2); _indices.add(3); _indices.add(0);
                break;
            case Faces.TOP:
                vertex.add(0.5f); vertex.add(0.5f); vertex.add(-0.5f);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(-0.5f); vertex.add(0.5f); vertex.add(-0.5f);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(-0.5f); vertex.add(0.5f); vertex.add(0.5f);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                vertex.add(0.5f); vertex.add(0.5f); vertex.add(0.5f);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f);
                _indices.add(0); _indices.add(1); _indices.add(2);
                _indices.add(2); _indices.add(3); _indices.add(0);
                break;
        }
        int prev_size = vertices.length;
        float[] prev_v_data = vertices;
        vertices = new float[prev_size+vertex.size()];
        System.arraycopy(prev_v_data, 0, vertices, 0, prev_v_data.length);

        for(int i = 0; i < vertex.size(); i++) {
            vertices[prev_size+i] = vertex.get(i);
        }

        prev_size = indices.length;
        int[] prev_i_data = indices;
        indices = new int[prev_size+_indices.size()];
        System.arraycopy(prev_i_data, 0, indices, 0, prev_i_data.length);

        int last;
        if(indices[1] == 0)
            last = findMax(indices);
        else
            last = findMax(indices)+1;

        for(int i = 0; i < _indices.size(); i++) {
            indices[prev_size+i] = _indices.get(i) + last;
        }
    }

    public static int findMax(int[] array) {
        if(array == null || array.length == 0) {
            throw new IllegalArgumentException("The array is null or his length is 0");
        }

        int max = array[0];
        for(int i = 0; i < array.length; i++) {
            if(array[i] > max)
                max = array[i];
        }
        return max;
    }
}
