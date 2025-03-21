package net.GameLayer;

import com.sudoplay.joise.module.*;
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
    public static int MAX_HEIGHT = 157;
    public static int MIN_HEIGHT = 40;
    public static int SURFACE_HEIGHT = 70;
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
        DEEPSLATE((byte)5),
        WATER((byte)6);

        private final byte id;

        BlockType(byte id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }

    public static void AddChunkSurface(Chunk chunk) {
        // Create the terrain height
        ModuleFractal baseTerrain = new ModuleFractal(
                ModuleFractal.FractalType.FBM, // Fractal type
                ModuleBasisFunction.BasisType.SIMPLEX, // Using SimplexNoise
                ModuleBasisFunction.InterpolationType.QUINTIC // fluid interpolation
        );
        baseTerrain.setSeed(World.seed);
        baseTerrain.setNumOctaves(4);

        ModuleScaleOffset surfaceScaleOffset = new ModuleScaleOffset();
        surfaceScaleOffset.setSource(baseTerrain);
        surfaceScaleOffset.setScale(0.2);
        surfaceScaleOffset.setOffset(0.3);

        // Clamping to avoid extreme values
        ModuleClamp clamping = new ModuleClamp();
        clamping.setSource(surfaceScaleOffset);
        clamping.setRange(0.0, 0.9);

        double surfaceFrequency = 1.0 / 128.0;
        double depthFrequency = 1.0 / 64.0;

        long worldX = chunk.positionX * ChunkGen.X_DIMENSION;
        long worldZ = chunk.positionZ * ChunkGen.Z_DIMENSION;

        // Add the surface
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int z = 0; z < Z_DIMENSION; z++) {

                double perlinX = (double) (worldX + x) * surfaceFrequency;
                double perlinY = (double) (worldZ + z) * surfaceFrequency;

                double baseHeight = clamping.get(perlinX, perlinY);

                // Final formula to calculate the height
                //int y = (int) (30 + (40 * baseHeight) + (87 * max(0, moutainFactor)));

                int y = (int) (MIN_HEIGHT + (SURFACE_HEIGHT * baseHeight));
                y = abs(Y_CHUNK) + min(MAX_HEIGHT, max(MIN_HEIGHT, y));
                chunk.blocks[x][y][z] = BlockType.STONE;

            }
        }

        // Add water to the surface
        for(int x = 0; x < ChunkGen.X_DIMENSION; x++) {
            for(int z = 0; z < ChunkGen.Z_DIMENSION; z++) {
                for(int y = WATER_LEVEL; y > MIN_HEIGHT; y--) {
                    int yy = y + abs(Y_CHUNK);
                    if(chunk.blocks[x][yy][z] == BlockType.STONE) break;
                    chunk.blocks[x][yy][z] = BlockType.WATER;
                }
            }
        }

        // Add the depth surface
        // Appliquer un scale et un offset pour mieux lisser le terrain
        ModuleScaleOffset scaleOffset = new ModuleScaleOffset();
        scaleOffset.setSource(World.basis);
        scaleOffset.setScale(0.1);
        scaleOffset.setOffset(0.1);

        ModuleClamp clamp = new ModuleClamp();
        clamp.setSource(scaleOffset);
        clamp.setRange(0.08, 1.0);

        for(int x = 0; x < X_DIMENSION; x++) {
            for(int z = 0; z < Z_DIMENSION; z++) {

                double perlinX = (double) (worldX + x) * depthFrequency;
                double perlinY = (double) (worldZ + z) * depthFrequency;

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
                        boolean isUnderWater = (chunk.blocks[x][y+5][z] == BlockType.WATER);
                        chunk.blocks[x][y][z] = (isUnderWater)? BlockType.GRAVEL :
                                (chunk.blocks[x][y+1][z] == BlockType.WATER)? BlockType.DIRT : BlockType.GRASS;

                        // Add sub surface blocks
                        for(int i = 1; i < dirtLevel; i++) {
                            chunk.blocks[x][y-i][z] = (isUnderWater)? BlockType.GRAVEL : BlockType.DIRT;
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

    public static boolean isChunkInFrustum(Camera.Plane[] frustumPlanes, double chunkX, double chunkZ) {
        float chunkSize = ChunkGen.X_DIMENSION; // Length of a chunk
        float chunkHeight = ChunkGen.Y_DIMENSION;
        float chunkY = ChunkGen.Y_CHUNK;

        // Try the 4 corners of a chunk (ground)
        double[][] corners = {
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
            for (double[] corner : corners) {
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
