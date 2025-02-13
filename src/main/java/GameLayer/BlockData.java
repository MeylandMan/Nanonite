package GameLayer;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;

public class BlockData {
    public static void createFaceVertices(Chunk chunk, Block block, Block.Faces face) {
        ArrayList<Byte> vertex = new ArrayList<>();
        switch (face) {
            case Block.Faces.FRONT:
                vertex.add((byte) (-0.5+block.positionX)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(0, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(0, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(0, block));
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(0, block));
                break;
            case Block.Faces.BACK:
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(1, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(1, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(1, block));
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(1, block));
                break;
            case Block.Faces.RIGHT:
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(2, block));
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(2, block));
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(2, block));
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(2, block));
                break;
            case Block.Faces.LEFT:
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(3, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(3, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(3, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(3, block));
                break;
            case Block.Faces.BOTTOM:
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(4, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(4, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(4, block));
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(-0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(4, block));
                break;
            case Block.Faces.TOP:
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(5, block));
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(-0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)0);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(5, block));
                vertex.add((byte)(-0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)1); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(5, block));
                vertex.add((byte)(0.5f+block.getPosition().x)); vertex.add((byte)(0.5f+block.getPosition().y)); vertex.add((byte)(0.5f+block.getPosition().z));       vertex.add((byte)0); vertex.add((byte)1);      vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)0); vertex.add((byte)getTextureIndex(5, block));
                break;
        }

        chunk.vertices = setVerticesData(chunk.vertices, vertex);
    }

    public static float getTextureIndex(int x, Block block) {
        if(x > 5) {
            throw new IllegalArgumentException("Texture index out of bounds");
        }

        float[] xx = new float[6];
        /*
                0 -- FRONT      0.f -- Dirt
                1 -- BACK       1.f -- Grass side
                2 -- RIGHT      2.f -- Grass top
                3 -- LEFT
                4 -- BOTTOM
                5 -- TOP
        */
        switch (block.type) {
            case AIR:
                // DO nothing since xx is already filled with 0
                break;
            case DIRT:
                // DO nothing since xx is already filled with 0
                break;
            case GRASS:
                xx[0] = 1.f;
                xx[1] = 1.f;
                xx[2] = 1.f;
                xx[3] = 1.f;
                xx[4] = 0.f;
                xx[5] = 2.f;
                break;
            default:
                System.out.println("Unknown block type: " + block.type);
                break;
        }
        return xx[x];
    }

    public static String getTexturePath(int x) {
        if(x > Chunk.TEXTURE_LOADED) {
            throw new IllegalArgumentException("Texture index out of bounds");
        }

        String[] xx = {
                "textures/blocks/dirt.png",
                "textures/blocks/grass_block_side.png",
                "textures/blocks/grass_block_top.png"
        };
        return xx[x];
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

    public static float[] setVerticesData(@NotNull byte[] vertices, @NotNull float[] vertex) {
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

    public static byte[] setVerticesData(@NotNull byte[] vertices, @NotNull ArrayList<Byte> vertex) {

        int prev_size = vertices.length;
        byte[] newData = new byte[prev_size + vertex.size()];

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
