package net.GameLayer;

import com.sudoplay.joise.module.*;
import net.Core.BlockModel;
import net.Core.Client;
import net.Core.Face;
import org.joml.Random;
import org.joml.Vector3f;

import java.util.Map;

import static org.joml.Math.*;

public class ChunkGen {
    public final static int Y_CHUNK = -64;

    public final static byte X_DIMENSION = 16;
    public final static int Y_DIMENSION = 16;
    public final static byte Z_DIMENSION = 16;

    // Surface data
    public static int MAX_HEIGHT = 157;
    public static int MIN_HEIGHT = 40;
    public static int SURFACE_HEIGHT = 50;
    public final static int WATER_LEVEL = 64;

    // Depths data
    public static int MAX_DEPTH_HEIGHT = 0;
    public static int MIN_DEPTH_HEIGHT = -256;
    public static int SURFACE_DEPTH_HEIGHT = 64;

    public enum BlockType {
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

        public int getID() {
            return id;
        }
    }

    public static boolean isChunkNearby(long x, long y, long z) {

        int radius = Client.renderDistance / 2;

        long chunkX = (long) (World.player.position.x / ChunkGen.X_DIMENSION);
        long chunkY = (long) (World.player.position.y / ChunkGen.Y_DIMENSION);
        long chunkZ = (long) (World.player.position.z / ChunkGen.Z_DIMENSION);

        long chunkDistX = abs(x - chunkX);
        long chunkDistY = abs(y - chunkY);
        long chunkDistZ = abs(z - chunkZ);

        if(chunkDistX >= radius || chunkDistY >= radius || chunkDistZ >= radius)
            return false;

        return true;
    }


    private static void AddSurface(Chunk chunk) {

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
        ModuleClamp clamping = new ModuleClamp();
        clamping.setSource(surfaceScaleOffset);
        clamping.setRange(0.0, 0.9);

        double surfaceFrequency = 1.0 / 171.03;

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

                int worldY = (int) (MIN_HEIGHT + (SURFACE_HEIGHT * baseHeight));
                worldY = min(MAX_HEIGHT, max(MIN_HEIGHT, worldY));

                // Find the corresponding Y chunk
                int chunkY = worldY / ChunkGen.Y_DIMENSION;
                int localY = worldY % ChunkGen.Y_DIMENSION;

                if(chunk.positionY != chunkY) continue;

                chunk.AddBlock(x, localY, z, BlockType.STONE);

            }
        }

        // Add water to the surface
        for(int x = 0; x < ChunkGen.X_DIMENSION; x++) {
            for(int z = 0; z < ChunkGen.Z_DIMENSION; z++) {
                for(int y = WATER_LEVEL; y > MIN_HEIGHT; y--) {

                    // Find the corresponding Y chunk
                    int chunkY = y / ChunkGen.Y_DIMENSION;
                    int localY = y % ChunkGen.Y_DIMENSION;

                    if(chunk.positionY != chunkY) continue;

                    if(chunk.blocks == null)
                        chunk.blocks = new BlockType[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];

                    if(chunk.blocks[x][localY][z] == BlockType.STONE) break;
                    chunk.AddBlock(x, localY, z, BlockType.WATER);
                }
            }
        }
    }

    private static void AddDepthSurface(Chunk chunk) {

        // Add the depth surface
        // Appliquer un scale et un offset pour mieux lisser le terrain
        ModuleScaleOffset scaleOffset = new ModuleScaleOffset();
        scaleOffset.setSource(World.basis);
        scaleOffset.setScale(0.1);
        scaleOffset.setOffset(0.1);

        ModuleClamp clamp = new ModuleClamp();
        clamp.setSource(scaleOffset);
        clamp.setRange(0.0, 1.0);

        long worldX = chunk.positionX * ChunkGen.X_DIMENSION;
        long worldZ = chunk.positionZ * ChunkGen.Z_DIMENSION;

        double depthFrequency = 1.0 / 64.0;

        for(int x = 0; x < X_DIMENSION; x++) {
            for(int z = 0; z < Z_DIMENSION; z++) {

                double perlinX = (double) (worldX + x) * depthFrequency;
                double perlinY = (double) (worldZ + z) * depthFrequency;

                double noiseValue = clamp.get(perlinX, perlinY);

                int y = (int) (MIN_DEPTH_HEIGHT + (SURFACE_DEPTH_HEIGHT * noiseValue));

                // Find the corresponding Y chunk
                int chunkY = y / ChunkGen.Y_DIMENSION;
                int localY = (y % ChunkGen.Y_DIMENSION + ChunkGen.Y_DIMENSION) % ChunkGen.Y_DIMENSION;

                if(chunk.positionY != chunkY) continue;

                chunk.AddBlock(x, localY, z, BlockType.STONE);
            }
        }
    }

    public static void AddChunkSurface(Chunk chunk) {

        if(chunk.positionY > 0)
            AddSurface(chunk);
        else
            AddDepthSurface(chunk);
    }

    public static void ResolveChunkSurface(Chunk chunk) {

        Random rand = new Random(World.seed);
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int z = 0; z < Z_DIMENSION; z++) {

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
                for(int i = MIN_DEPTH_HEIGHT; i < MAX_DEPTH_HEIGHT; i++) {
                    int y = i;

                    // Find the corresponding Y chunk
                    int chunkY = y / ChunkGen.Y_DIMENSION;
                    int localY = y % ChunkGen.Y_DIMENSION;

                    if(chunk.positionY != chunkY) continue;

                    if(chunk.blocks == null)
                        chunk.blocks = new BlockType[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];

                    if(chunk.blocks[x][localY][z] == BlockType.STONE) {
                        chunk.AddBlock(x, localY, z, BlockType.DEEPSLATE);
                        break;
                    }

                    if(chunk.blocks[x][localY][z] != BlockType.BEDROCK) {
                        chunk.AddBlock(x, localY, z, BlockType.DEEPSLATE);
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

    public static boolean isChunkInFrustum(Camera.Plane[] frustumPlanes, double chunkX, double chunkY, double chunkZ) {
        float chunkSize = ChunkGen.X_DIMENSION; // Length of a chunk
        float chunkHeight = ChunkGen.Y_DIMENSION;

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
        //ResolveChunkSurface(chunk);
    }
}
