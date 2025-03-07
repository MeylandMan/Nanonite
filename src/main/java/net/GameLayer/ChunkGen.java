package net.GameLayer;

import net.Core.BlockModel;
import net.Core.Client;
import net.Core.Element;
import net.Core.Face;
import org.joml.Random;
import org.lwjgl.system.MemoryUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.random.*;

import static org.joml.Math.*;

public class ChunkGen {
    public final static int Y_CHUNK = 0;

    public final static byte X_DIMENSION = 16;
    public final static int Y_DIMENSION = 256 + abs(Y_CHUNK);
    public final static byte Z_DIMENSION = 16;
    public final static int Y_MAX = 5;

    public enum BlockType {
        DIRT((byte)0),
        GRASS((byte)1);

        private final byte id;

        BlockType(byte id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }

    public static void AddChunkSurface(Chunk chunk) {
        Random rand = new Random();
        int temp = rand.nextInt(Y_MAX);

        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_MAX + temp; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    chunk.blocks[x][y][z] = (y == Y_MAX-1)? BlockType.GRASS : BlockType.DIRT;

                    BlockModel model = Client.modelLoader.getModel(Client.modelPaths[chunk.blocks[x][y][z].getID()]);
                    chunk.blockDrawn += model.getElements().size();
                }
            }
        }
    }

    public static int getBlocksNumber(Chunk chunk) {
        int x = 0;
        for(BlockType[][] blocks : chunk.blocks) {
            for(BlockType[] block : blocks) {
                for(BlockType block1 : block) {
                    if(block1 == null) continue;

                    BlockModel model = Client.modelLoader.getModel(Client.modelPaths[block1.getID()]);
                    x+= model.getElements().size();
                }
            }

        }
        return x;
    }
    public static void ResolveChunkSurface(Chunk chunk) {
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    if(chunk.blocks[x][y][z] == null)
                        continue;
                    if(chunk.blocks[x][y][z] == BlockType.DIRT) {
                        if(chunk.blocks[x][y+1][z] == null)
                            chunk.blocks[x][y][z] = BlockType.GRASS;
                    }
                }
            }
        }
    }

    protected static float getTextureID(BlockModel model, Map.Entry<String, Face> face) {

        String modelTexture = model.getTexture(face.getKey());
        face.getValue().setTexture(modelTexture);
        String texture = face.getValue().getTexture();
        for(int i = 0; i < Client.blockTexturePath.size(); i++) {
            if(texture.equals(Client.blockTexturePath.get(i))) {
                return (float)i;
            }
        }
        return -1.0f; // In case the path is not found
    }

    protected static void setupChunk(Chunk chunk) {

       chunk.blocks = new BlockType[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];

        // Add Blocks inside the chunk
        AddChunkSurface(chunk);

        // check if there's blocks at the top of the dirt
        ResolveChunkSurface(chunk);

        //CompressChunkData(chunk);
    }

    protected static void CompressChunkData(Chunk chunk) {
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {

                }
            }
        }
    }
}
