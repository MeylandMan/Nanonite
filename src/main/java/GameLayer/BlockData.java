package GameLayer;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;

public class BlockData {
    public static void createFaceVertices(Chunk chunk, Block block, Block.Faces face) {
        ArrayList<Float> vertex = new ArrayList<>();
        switch (face) {
            case Block.Faces.FRONT:
                vertex.add(-0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(0));
                vertex.add(0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(0));
                vertex.add(0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(0));
                vertex.add(-0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(0));
                break;
            case Block.Faces.BACK:
                vertex.add(-0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(1));
                vertex.add(0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(1));
                vertex.add(0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(1));
                vertex.add(-0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(1));
                break;
            case Block.Faces.RIGHT:
                vertex.add(-0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(2));
                vertex.add(-0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(2));
                vertex.add(-0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(2));
                vertex.add(-0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(2));
                break;
            case Block.Faces.LEFT:
                vertex.add(0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(3));
                vertex.add(0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(3));
                vertex.add(0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(3));
                vertex.add(0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(3));
                break;
            case Block.Faces.BOTTOM:
                vertex.add(-0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(5));
                vertex.add(0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(5));
                vertex.add(0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(5));
                vertex.add(-0.5f+block.getPosition().x); vertex.add(-0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(5));
                break;
            case Block.Faces.TOP:
                vertex.add(0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(4));
                vertex.add(-0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(-0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(0.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(4));
                vertex.add(-0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(1.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(4));
                vertex.add(0.5f+block.getPosition().x); vertex.add(0.5f+block.getPosition().y); vertex.add(0.5f+block.getPosition().z);       vertex.add(0.0f); vertex.add(1.0f);      vertex.add(0.0f); vertex.add(0.0f); vertex.add(0.0f); vertex.add(block.getTextureIndex(4));
                break;
        }

        chunk.vertices = setVerticesData(chunk.vertices, vertex);

    }

    public static void createFaceIndices(Chunk chunk, @NotNull Block.Faces face) {
        ArrayList<Integer> _indices = new ArrayList<>();
        switch (face) {
            case Block.Faces.FRONT:
                _indices.add(0); _indices.add(3); _indices.add(2);
                _indices.add(2); _indices.add(1); _indices.add(0);
                break;
            case Block.Faces.BACK:
                _indices.add(0); _indices.add(1); _indices.add(2);
                _indices.add(2); _indices.add(3); _indices.add(0);
                break;
            case Block.Faces.RIGHT:
                _indices.add(0); _indices.add(1); _indices.add(2);
                _indices.add(2); _indices.add(3); _indices.add(0);
                break;
            case Block.Faces.LEFT:
               _indices.add(0); _indices.add(1); _indices.add(2);
                _indices.add(2); _indices.add(3); _indices.add(0);
                break;
            case Block.Faces.BOTTOM:
               _indices.add(0); _indices.add(1); _indices.add(2);
                _indices.add(2); _indices.add(3); _indices.add(0);
                break;
            case Block.Faces.TOP:
                _indices.add(0); _indices.add(1); _indices.add(2);
                _indices.add(2); _indices.add(3); _indices.add(0);
                break;
        }
        chunk.indices = setIndicesData(chunk.indices, _indices);

    }

    public static float[] setVerticesData(@NotNull float[] vertices, @NotNull float[] vertex) {
        int prev_size = vertices.length;
        float[] newData = new float[prev_size + vertex.length];

        System.arraycopy(vertices, 0, newData, 0, prev_size);

        for (int i = 0; i < vertex.length; i++) {
            newData[prev_size + i] = vertex[i];
        }

        return newData;
    }

    public static int[] setIndicesData(@NotNull int[] indices, @NotNull int[] _indices) {
        int last;
        if(indices[1] == 0)
            last = findMax(indices);
        else
            last = findMax(indices)+1;

        int prev_size = indices.length;
        int[] newData = new int[prev_size + _indices.length];

        System.arraycopy(indices, 0, newData, 0, prev_size);

        for (int i = 0; i < _indices.length; i++) {
            newData[prev_size + i] = _indices[i] + last;
        }

        return newData;
    }

    public static float[] setVerticesData(@NotNull float[] vertices, @NotNull ArrayList<Float> vertex) {

        int prev_size = vertices.length;
        float[] newData = new float[prev_size + vertex.size()];

        System.arraycopy(vertices, 0, newData, 0, prev_size);

        for (int i = 0; i < vertex.size(); i++) {
            newData[prev_size + i] = vertex.get(i);
        }

        return newData;
    }

    public static int[] setIndicesData(@NotNull int[] indices, @NotNull ArrayList<Integer> _indices) {

        int last = 0;
        if(indices.length > 1) {
            if(indices[1] == 0)
                last = findMax(indices);
            else
                last = findMax(indices)+1;
        }


        int prev_size = indices.length;
        int[] newData = new int[prev_size + _indices.size()];

        System.arraycopy(indices, 0, newData, 0, prev_size);

        for (int i = 0; i < _indices.size(); i++) {
            newData[prev_size + i] = _indices.get(i) + last;
        }

        return newData;
    }

    public static int findMax(int[] array) {
        if(array == null || array.length == 0) {
            throw new IllegalArgumentException("The array is null or his length is 0");
        }

        int max = array[0];
        for (int j : array) {
            max = Math.max(max, j);
        }
        return max;
    }
}
