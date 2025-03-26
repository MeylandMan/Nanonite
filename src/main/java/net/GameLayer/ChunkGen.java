package net.GameLayer;

import com.sudoplay.joise.module.*;
import net.Core.BlockModel;
import net.Core.Client;
import net.Core.Face;
import org.joml.Random;

import java.util.Arrays;
import java.util.Map;

import static org.joml.Math.*;

public class ChunkGen {

    public final static byte SIZE = 16;
    public static byte[] DefaultChunk = new byte[SIZE * SIZE * SIZE];


    // Surface data
    public static int MAX_HEIGHT = 157;
    public static int MIN_HEIGHT = 40;
    public static int SURFACE_HEIGHT = 50;
    public final static int WATER_LEVEL = 64;

    // Depths data
    public static int MAX_DEPTH_HEIGHT = 0;
    public static int MIN_DEPTH_HEIGHT = -256;
    public static int SURFACE_DEPTH_HEIGHT = 64;


    // Surface Terrain Simplex noise
    static ModuleClamp surfaceClamp = new ModuleClamp();
    static ModuleClamp depthClamp = new ModuleClamp();

    public enum BlockType {
        AIR((byte)-1),
        DIRT((byte)0),
        GRASS((byte)1),
        STONE((byte)2),
        GRAVEL((byte)3),
        BEDROCK((byte)4),
        DEEPSLATE((byte)5),
        WATER((byte)6),
        SAND((byte)7);

        private final byte id;

        BlockType(byte id) {
            this.id = id;
        }

        public byte getID() {
            return id;
        }
    }

    public static void Init() {

        //Initialize chunk default cache
        Arrays.fill(DefaultChunk, BlockType.AIR.getID());

        // Create the terrain height
        ModuleFractal baseTerrain = new ModuleFractal(
                ModuleFractal.FractalType.FBM, // Fractal type
                ModuleBasisFunction.BasisType.SIMPLEX, // Using SimplexNoise
                ModuleBasisFunction.InterpolationType.QUINTIC // fluid interpolation
        );
        baseTerrain.setSeed(World.seed);
        baseTerrain.setNumOctaves(5);

        ModuleScaleOffset surfaceScaleOffset = new ModuleScaleOffset();
        surfaceScaleOffset.setSource(baseTerrain);
        surfaceScaleOffset.setScale(0.25);
        surfaceScaleOffset.setOffset(0.6);

        // Clamping to avoid extreme values
        surfaceClamp.setSource(surfaceScaleOffset);
        surfaceClamp.setRange(0.0, 0.9);

        // Appliquer un scale et un offset pour mieux lisser le terrain
        ModuleScaleOffset scaleOffset = new ModuleScaleOffset();
        scaleOffset.setSource(World.basis);
        scaleOffset.setScale(0.1);
        scaleOffset.setOffset(0.1);

        depthClamp.setSource(scaleOffset);
        depthClamp.setRange(0.0, 1.0);

    }

    public static int index(int x , int y, int z) {
        return x + (z * SIZE) + (y * SIZE * SIZE);
    }

    private static void AddSurface(Chunk chunk) {

        double surfaceFrequency = 1.0 / 171.03;

        long worldX = chunk.positionX * ChunkGen.SIZE;
        long worldZ = chunk.positionZ * ChunkGen.SIZE;

        // Add the surface
        for(int x = 0; x < SIZE; x++) {
            for(int z = 0; z < SIZE; z++) {

                double perlinX = (double) (worldX + x) * surfaceFrequency;
                double perlinY = (double) (worldZ + z) * surfaceFrequency;

                double baseHeight = surfaceClamp.get(perlinX, perlinY);

                // Final formula to calculate the height
                //int y = (int) (30 + (40 * baseHeight) + (87 * max(0, moutainFactor)));

                int worldY = (int) (MIN_HEIGHT + (SURFACE_HEIGHT * baseHeight));
                worldY = min(MAX_HEIGHT, max(MIN_HEIGHT, worldY));

                // Find the corresponding Y chunk
                int chunkY = worldY / ChunkGen.SIZE;
                int localY = worldY % ChunkGen.SIZE;

                if(chunk.positionY != chunkY) continue;

                chunk.AddBlock(x, localY, z, BlockType.STONE);

            }
        }

        // Add water to the surface
        for(int x = 0; x < SIZE; x++) {
            for(int z = 0; z < SIZE; z++) {
                for(int y = WATER_LEVEL; y > MIN_HEIGHT; y--) {

                    // Find the corresponding Y chunk
                    int chunkY = y / SIZE;
                    int localY = y % SIZE;

                    if(chunk.positionY != chunkY) continue;

                    if(chunk.blocks == null)
                        chunk.CreateBlocksArray();

                    if(chunk.getBlock(x,localY,z) == BlockType.STONE.getID()) break;
                    chunk.AddBlock(x, localY, z, BlockType.WATER);
                }
            }
        }
    }

    private static void AddDepthSurface(Chunk chunk) {

        // Add the depth surface

        long worldX = chunk.positionX * ChunkGen.SIZE;
        long worldZ = chunk.positionZ * ChunkGen.SIZE;

        double depthFrequency = 1.0 / 64.0;

        for(int x = 0; x < SIZE; x++) {
            for(int z = 0; z < SIZE; z++) {

                double perlinX = (double) (worldX + x) * depthFrequency;
                double perlinY = (double) (worldZ + z) * depthFrequency;

                double noiseValue = depthClamp.get(perlinX, perlinY);

                int y = (int) (MIN_DEPTH_HEIGHT + (SURFACE_DEPTH_HEIGHT * noiseValue));

                // Find the corresponding Y chunk
                int chunkY = y / ChunkGen.SIZE;
                int localY = (y % ChunkGen.SIZE + ChunkGen.SIZE) % ChunkGen.SIZE;

                if(chunk.positionY != chunkY) continue;

                chunk.AddBlock(x, localY, z, BlockType.STONE);
            }
        }
    }

    public static void AddChunkSurface(Chunk chunk) {

        if(chunk.positionY > MAX_DEPTH_HEIGHT)
            AddSurface(chunk);
        else
            AddDepthSurface(chunk);
    }

    public static void ResolveChunkSurface(Chunk chunk) {

        Random rand = new Random(World.seed);
        int dirtLevel = rand.nextInt(5)+1;

        for(int x = 0; x < SIZE; x++) {
            for(int z = 0; z < SIZE; z++) {

                double surfaceFrequency = 1.0 / 171.03;

                long worldX = chunk.positionX * ChunkGen.SIZE;
                long worldZ = chunk.positionZ * ChunkGen.SIZE;

                double perlinX = (double) (worldX + x) * surfaceFrequency;
                double perlinY = (double) (worldZ + z) * surfaceFrequency;

                double baseHeight = surfaceClamp.get(perlinX, perlinY);

                int worldY = (int) (MIN_HEIGHT + (SURFACE_HEIGHT * baseHeight));
                worldY = min(MAX_HEIGHT, max(MIN_HEIGHT, worldY));

                // Find the corresponding Y chunk
                int chunkY = worldY / ChunkGen.SIZE;
                int localY = worldY % ChunkGen.SIZE;

                if(chunk.positionY != chunkY) continue;

                if(chunk.getBlock(x, localY, z) == BlockType.STONE.getID()) {
                    BlockType block = (worldY <= WATER_LEVEL)?
                            (worldY <= WATER_LEVEL-5)? BlockType.GRAVEL : BlockType.SAND :
                            (worldY <= WATER_LEVEL+2)? BlockType.SAND : BlockType.GRASS;
                    chunk.ReplaceBlock(x, localY, z, block);
                }

                /*
                int dirtLevel = max(3, rand.nextInt(5) + 1);
                int stoneLevel = max(MAX_DEPTH_HEIGHT-4,rand.nextInt(MAX_DEPTH_HEIGHT) + 1);

                // Add surface blocks
                for(int y = Y_DIMENSION-1; y > MAX_DEPTH_HEIGHT; y--) {
                    if(chunk.blocks[x][y][z] == BlockType.STONE) {
                        boolean isUnderWater = (chunk.blocks[x][y+5][z] == BlockType.WATER);
                        chunk.blocks[x][y][z] = (isUnderWater)? BlockType.GRAVEL :
                                (chunk.blocks[x][y+1][z] == BlockType.WATER)? BlockType.SAND :
                                        (y == WATER_LEVEL)? BlockType.SAND : BlockType.GRASS;

                        // Add sub surface blocks
                        for(int i = 1; i < dirtLevel; i++) {
                            int yy = y-i;
                            chunk.blocks[x][yy][z] = (isUnderWater)? BlockType.GRAVEL :
                                    (chunk.blocks[x][yy+1][z] == BlockType.SAND) ? BlockType.SAND : BlockType.DIRT;
                        }

                        // Add stone blocks
                        for(int i = 0; i < stoneLevel; i++) {
                            int yy = y-dirtLevel-i;
                            chunk.blocks[x][yy][z] = BlockType.STONE;
                        }
                        break;
                    }
                }
                */

                // Add Deepslate
                /*
                for(int i = MIN_DEPTH_HEIGHT; i < MAX_DEPTH_HEIGHT; i++) {
                    int y = i;

                    // Find the corresponding Y chunk
                    int chunkY = y / ChunkGen.SIZE;
                    int localY = y % ChunkGen.SIZE;

                    if(chunk.positionY != chunkY) continue;

                    if(chunk.blocks == null)
                        chunk.blocks = new BlockType[SIZE][SIZE][SIZE];

                    if(chunk.blocks[x][localY][z] == BlockType.STONE) {
                        chunk.AddBlock(x, localY, z, BlockType.DEEPSLATE);
                        break;
                    }

                    if(chunk.blocks[x][localY][z] != BlockType.BEDROCK) {
                        chunk.AddBlock(x, localY, z, BlockType.DEEPSLATE);
                    }
                }
                */

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

    public static boolean isChunkInFrustum(Camera.Plane[] frustumPlanes, double chunkX, double chunkY, double chunkZ) {
        float chunkSize = ChunkGen.SIZE; // Length of a chunk
        float chunkHeight = ChunkGen.SIZE;

        // Try the 4 corners of a chunk (ground)
        double[][] corners = {
                {chunkX, chunkY, chunkZ},                                    // Bas-gauche
                {chunkX + chunkSize, chunkY, chunkZ},                        // Bas-droite
                {chunkX, chunkY, chunkZ + chunkSize},                        // Bas-gauche arrière
                {chunkX + chunkSize, chunkY, chunkZ + chunkSize},            // Bas-droite arrière
                {chunkX, chunkY + chunkSize, chunkZ},                               // Haut-gauche
                {chunkX + chunkSize, chunkY + chunkSize, chunkZ},                   // Haut-droite
                {chunkX, chunkY + chunkSize, chunkZ + chunkSize},                   // Haut-gauche arrière
                {chunkX + chunkSize, chunkY + chunkSize, chunkZ + chunkSize}        // Haut-droite arrière
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

        AddChunkSurface(chunk);
        ResolveChunkSurface(chunk);
    }
}
