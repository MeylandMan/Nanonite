package net.GameLayer;

import com.sudoplay.joise.module.ModuleClamp;
import com.sudoplay.joise.module.ModuleScaleOffset;
import net.Core.BlockModel;
import net.Core.Client;
import net.Core.Face;
import org.joml.Random;

import java.util.Map;

import static org.joml.Math.*;

public class ChunkGen {
    public final static int Y_CHUNK = -64;

    public final static byte X_DIMENSION = 16;
    public final static int Y_DIMENSION = 256 + abs(Y_CHUNK);
    public final static byte Z_DIMENSION = 16;

    // Surface data
    public static int MAX_HEIGHT = 92;
    public static int MIN_HEIGHT = 8;
    public static int SURFACE_HEIGHT = 30;
    public final static int WATER_LEVEL = 64;

    // Depths data
    public static int MAX_DEPTH_HEIGHT = 64;
    public static int MIN_DEPTH_HEIGHT = 1;
    //public static int SURFACE_DEPTH_HEIGHT = 10;

    public enum BlockType {
        DIRT((byte)0),
        GRASS((byte)1),
        STONE((byte)2),
        GRAVEL((byte)3),
        BEDROCK((byte)4),
        DEEPSLATE((byte)5);

        private final byte id;

        BlockType(byte id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }

    public static void AddChunkSurface(Chunk chunk) {

        // Appliquer un scale et un offset pour mieux lisser le terrain
        ModuleScaleOffset scaleOffset = new ModuleScaleOffset();
        scaleOffset.setSource(World.basis);
        scaleOffset.setScale(0.1);
        scaleOffset.setOffset(0.1);

        ModuleClamp clamp = new ModuleClamp();
        clamp.setSource(scaleOffset);
        clamp.setRange(0.01, 1.0);

        double surfaceFrequency = 1.0 / 128.0;
        double depthFrequency = 1.0 / 64.0;
        // Add the surface
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int z = 0; z < Z_DIMENSION; z++) {

                double perlinX = (double) (chunk.positionX + x) * surfaceFrequency;
                double perlinY = (double) (chunk.positionZ + z) * surfaceFrequency;

                double noiseValue = clamp.get(perlinX, perlinY);
                int y = (int) (SURFACE_HEIGHT + Math.round(MAX_HEIGHT + (MAX_HEIGHT - MIN_HEIGHT) * noiseValue));
                chunk.blocks[x][y][z] = BlockType.STONE;

            }
        }

        // Add the depth surface
        clamp.setRange(0.08, 1.0);
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int z = 0; z < Z_DIMENSION; z++) {

                double perlinX = (double) (chunk.positionX + x) * depthFrequency;
                double perlinY = (double) (chunk.positionZ + z) * depthFrequency;

                double noiseValue = clamp.get(perlinX, perlinY);
                int y = (int) Math.round(MAX_DEPTH_HEIGHT * noiseValue);
                chunk.blocks[x][y][z] = BlockType.STONE;
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

        Random rand = new Random(World.seed);
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int z = 0; z < Z_DIMENSION; z++) {
                int dirtLevel = max(3, rand.nextInt(5) + 1);
                int stoneLevel = max(MAX_DEPTH_HEIGHT-4,rand.nextInt(MAX_DEPTH_HEIGHT) + 1);

                // Add surface blocks
                for(int y = Y_DIMENSION-1; y > MAX_DEPTH_HEIGHT; y--) {
                    if(chunk.blocks[x][y][z] == BlockType.STONE) {
                        chunk.blocks[x][y][z] = BlockType.GRASS;

                        // Add sub surface blocks
                        for(int i = 1; i < dirtLevel; i++) {
                            chunk.blocks[x][y-i][z] = BlockType.DIRT;
                        }

                        // Add stone blocks
                        for(int i = 0; i < stoneLevel; i++) {
                            int yy = y-dirtLevel-i;
                            chunk.blocks[x][yy][z] = BlockType.STONE;
                        }
                        break;
                    }
                }

                // Add depths blocks
                int bedrockLevel = max(2, rand.nextInt(5) + 1);

                // Add bedrock
                for(int i = 0; i < bedrockLevel; i++) {
                    chunk.blocks[x][i][z] = BlockType.BEDROCK;
                }

                // Add Deepslate
                for(int i = 0; i < MAX_DEPTH_HEIGHT; i++) {
                    if(chunk.blocks[x][i][z] == BlockType.STONE) {
                        chunk.blocks[x][i][z] = BlockType.DEEPSLATE;
                        break;
                    }

                    if(chunk.blocks[x][i][z] != BlockType.BEDROCK) {
                        chunk.blocks[x][i][z] = BlockType.DEEPSLATE;
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

    public static boolean isChunkInFrustum(Camera.Plane[] frustumPlanes, float chunkX, float chunkZ) {
        float chunkSize = ChunkGen.X_DIMENSION; // Length of a chunk
        float chunkHeight = 80;
        float chunkY = ChunkGen.Y_CHUNK;

        // Try the 4 corners of a chunk (ground)
        float[][] corners = {
                {chunkX, chunkY, chunkZ},                                    // Bas-gauche
                {chunkX + chunkSize, chunkY, chunkZ},                        // Bas-droite
                {chunkX, chunkY, chunkZ + chunkSize},                        // Bas-gauche arrière
                {chunkX + chunkSize, chunkY, chunkZ + chunkSize},            // Bas-droite arrière
                {chunkX, chunkHeight, chunkZ},                               // Haut-gauche
                {chunkX + chunkSize, chunkHeight, chunkZ},                   // Haut-droite
                {chunkX, chunkHeight, chunkZ + chunkSize},                   // Haut-gauche arrière
                {chunkX + chunkSize, chunkHeight, chunkZ + chunkSize}        // Haut-droite arrière
        };

        for (Camera.Plane plane : frustumPlanes) {
            boolean inside = false;
            for (float[] corner : corners) {
                if (plane.isPointInside(corner[0], corner[1], corner[2])) {
                    inside = true;
                    break;
                }
            }

            if (!inside) return false; // If no corners is inside the view Frustum, eject the chunk
        }

        return true;
    }

    protected static void setupChunk(Chunk chunk) {

       chunk.blocks = new BlockType[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];

        // Add Terrain Surface
        AddChunkSurface(chunk);

        // check if there's blocks at the top of the dirt
        ResolveChunkSurface(chunk);
    }
}
