package GameLayer;

import Mycraft.Core.Camera;
import Mycraft.Core.Client;
import Mycraft.Core.MultiThreading;
import Mycraft.Debug.Debugger;
import Mycraft.Debug.Logger;
import GameLayer.Entities.Entity;
import Mycraft.Core.Player;
import Mycraft.Models.BlockModel;
import Mycraft.Models.Element;
import Mycraft.Models.Face;
import Mycraft.Physics.CubeCollision;
import Mycraft.Rendering.Shader;
import Mycraft.Rendering.WorldEnvironment;
import org.joml.*;
import org.lwjgl.system.MemoryUtil;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sudoplay.joise.module.ModuleBasisFunction;

import static org.joml.Math.abs;
import static org.joml.Math.floor;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;

public class World {
    public static ArrayList<CubeCollision> worldCollisions;
    Shader[] ChunkShaders = new Shader[2];
    public Shader shader;
    Shader EntitiesShader;
    //public static final float GRAVITY = 0;
    public static Player player;
    public static long ChunkDrawCalls = 0;
    public static boolean QueueQuery = true;

    public static Vector3d SpawnPoint = new Vector3d(8, 100, 8);
    // Procedural generation data's
    static ModuleBasisFunction basis;
    public static long seed;

    // Entities
    ArrayList<Entity> entities = new ArrayList<>();

    // Chunks datas
    public static final Map<Vector3f, Chunk> loadedChunks = new HashMap<>();
    public static final Map<Vector3f, Vector3f> loadedChunksID = new HashMap<>();

    // File des chunks à supprimer dans le thread OpenGL
    private static final ConcurrentLinkedQueue<Chunk> chunkDeletionQueue = new ConcurrentLinkedQueue<>();


    // Frustum Data
    public static Camera.Plane[] frustumPlanes;
    public static Vector3d min = new Vector3d();
    public static Vector3d max = new Vector3d();

    static double[] chunkQueueSpeed = new double[2];

    // Chunk Queues
    public static final PriorityQueue<ChunkDistance> chunksPos = new PriorityQueue<>(Comparator.comparingDouble(c -> c.distance));
    private static final Queue<Chunk> chunksToRemove = new LinkedList<>();

    // Macros
    private static final int MAX_CHUNKS_TO_REMOVE_PER_FRAME = 256;
    private static final int MAX_CHUNKS_TO_RENDER_PER_FRAME = 16;

    protected static class ChunkDistance {
        Chunk chunk;
        float distance;

        ChunkDistance(Chunk chunk, float distance) {
            this.chunk = chunk;
            this.distance = distance;
        }
    }

    public World() {
        worldCollisions = new ArrayList<>();
        shader = new Shader();
        shader.CreateShader("Physics.vert", "Physics.frag");

        Random rand = new Random();
        seed = rand.nextLong();
        Logger.log(Logger.Level.INFO, "World seed: " + seed);

        basis = new ModuleBasisFunction();
        basis.setType(ModuleBasisFunction.BasisType.SIMPLEX);
        basis.setSeed(seed);

        ChunkGen.Init();
        ChunkShaders[0] = new Shader();
        ChunkShaders[1] = new Shader();
        EntitiesShader = new Shader();

        ChunkShaders[0].CreateShader("Chunk.comp", "Chunk.frag");
        ChunkShaders[1].CreateShader("Liquid.comp", "Liquid.frag");
        EntitiesShader.CreateShader("Entity.comp", "Entity.frag");

        player = new Player(new Vector3d(SpawnPoint));
        player.setModel("entities/player");

        entities.add(player);
    }

    public World(long sd) {
        worldCollisions = new ArrayList<>();
        shader = new Shader();
        shader.CreateShader("Physics.vert", "Physics.frag");
        seed = sd;
        Logger.log(Logger.Level.INFO, "World seed: " + seed);

        basis = new ModuleBasisFunction();
        basis.setType(ModuleBasisFunction.BasisType.SIMPLEX);
        basis.setSeed(seed);

        ChunkGen.Init();
        ChunkShaders[0] = new Shader();
        ChunkShaders[1] = new Shader();
        EntitiesShader = new Shader();

        ChunkShaders[0].CreateShader("Chunk.comp", "Chunk.frag");
        ChunkShaders[1].CreateShader("Liquid.comp", "Liquid.frag");
        EntitiesShader.CreateShader("Entity.comp", "Entity.frag");

        player = new Player(new Vector3d(SpawnPoint));
        player.setModel("entities/player");

        entities.add(player);
    }

    public static void addChunksToQueue(boolean reset) {

        chunkQueueSpeed[0] = System.nanoTime();

        if(reset) {

            QueueQuery = true;
            chunkDeletionQueue.addAll(loadedChunks.values());
            loadedChunks.clear();
            chunksPos.clear();

            Logger.log(Logger.Level.INFO, "Deleted previous chunks");
            Logger.log(Logger.Level.INFO, "Loading chunks...");
        }

        long chunkX = (long) ChunkGen.getLocalChunk(player.position).x;
        long chunkY = (long) ChunkGen.getLocalChunk(player.position).y;
        long chunkZ = (long) ChunkGen.getLocalChunk(player.position).z;

        int radius = Client.renderDistance / 2;

        // List of chunks
        List<Future<?>> futures = new ArrayList<>();

        for (int dz = -radius; dz <= radius; dz++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {

                    long localX = (chunkX + dx);
                    long localY = (chunkY + dy);
                    long localZ = (chunkZ + dz);

                    Vector3f chunkID = new Vector3f(localX, localY, localZ);

                    if (loadedChunks.containsKey(chunkID) || !ChunkGen.isChunkInFrustum(frustumPlanes,
                            localX * ChunkGen.CHUNK_SIZE,
                            localY * ChunkGen.CHUNK_SIZE,
                            localZ * ChunkGen.CHUNK_SIZE
                    )) continue;

                    int finalDx = dx;
                    int finalDy = dy;
                    int finalDz = dz;

                    futures.add(MultiThreading.submitChunkTask(() -> {

                        Chunk chunk = new Chunk(localX, localY, localZ);
                        ChunkGen.setupChunk(chunk);
                        synchronized (loadedChunks) {
                            loadedChunks.put(chunkID, chunk);
                        }

                        float distanceSquared = finalDx * finalDx + finalDy * finalDy + finalDz * finalDz;
                        if(chunk.blocks != null) {
                            synchronized (chunksPos) {
                                chunksPos.add(new ChunkDistance(chunk, distanceSquared));
                            }

                            synchronized (loadedChunksID) {
                                loadedChunksID.put(chunkID, chunkID);
                            }
                        }

                        return null;
                    }));
                }
            }
        }

        for(Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        if(Debugger.debug && QueueQuery) {
            QueueQuery = false;
            chunkQueueSpeed[1] = System.nanoTime();
            double result = (chunkQueueSpeed[1] - chunkQueueSpeed[0]) / 1_000_000.0;
            Logger.log(Logger.Level.INFO, "Chunk queue filled in " + String.format("%.3f", result) + "ms");
            Logger.log(Logger.Level.INFO, "Rendering chunks...");
        }
    }


    public static void updateNearbyChunks(Vector3f v) {

        Vector3f[] neighbors = {
                new Vector3f(v.x + 1, v.y, v.z),
                new Vector3f(v.x - 1, v.y, v.z),
                new Vector3f(v.x, v.y + 1, v.z),
                new Vector3f(v.x, v.y - 1, v.z),
                new Vector3f(v.x, v.y, v.z - 1),
                new Vector3f(v.x, v.y, v.z + 1)
        };

        for (Vector3f neighbor : neighbors) {
            Chunk neighborChunk = loadedChunks.get(neighbor);
            if (neighborChunk != null) {
                 if (neighborChunk.StaticBlocks == null) neighborChunk.Init();
                neighborChunk.updateChunk = true;
                neighborChunk.updateChunk((long) neighbor.x, (long) neighbor.y, (long) neighbor.z);
            }
        }
    }

    public void loadChunks() {

        long chunkX = (long) ChunkGen.getLocalChunk(player.position).x;
        long chunkY = (long) ChunkGen.getLocalChunk(player.position).y;
        long chunkZ = (long) ChunkGen.getLocalChunk(player.position).z;
        int radius = Client.renderDistance / 2;

        for (int i = 0; i < MAX_CHUNKS_TO_RENDER_PER_FRAME && !chunksPos.isEmpty(); i++) {

            Chunk getID = chunksPos.poll().chunk;
            if (getID == null) break;

            long chunkDistX = abs(getID.positionX - chunkX);
            long chunkDistY = abs(getID.positionY - chunkY);
            long chunkDistZ = abs(getID.positionZ - chunkZ);
            if (chunkDistX > radius || chunkDistZ > radius || chunkDistY > radius) {
                continue;
            }

            Vector3f chunkID = new Vector3f(getID.positionX, getID.positionY, getID.positionZ);
            Chunk chunk = loadedChunks.get(chunkID);

            if (chunk == null) continue;
            if (chunk.StaticBlocks == null) chunk.Init();

            //updateNearbyChunks(chunkID);
            chunk.updateChunk((long) chunkID.x, (long) chunkID.y, (long) chunkID.z);
        }

    }

    protected static FloatBuffer getChunkData(long xx, long yy, long zz, int type) {
        Vector3f chunkID = new Vector3f(xx, yy, zz);
        Chunk chunk = loadedChunks.get(chunkID);

        // 3 positions + 3 min + 3 max + 1 texID + 1 FaceID + 4 Ambient Occlusion
        int estimatedSizeBuffer = (chunk.blockDrawn * 6 * 15);
        FloatBuffer buffer = MemoryUtil.memAllocFloat(estimatedSizeBuffer);


        for(int x = 0; x < ChunkGen.CHUNK_SIZE; x++) {
            for(int y = 0; y < ChunkGen.CHUNK_SIZE; y++) {
                for(int z = 0; z < ChunkGen.CHUNK_SIZE; z++) {
                    if(chunk.getBlock(x,y,z) == ChunkGen.BlockType.AIR.getID())
                        continue;

                    if(type == 0 && chunk.getBlock(x,y,z) == ChunkGen.BlockType.WATER.getID())
                        continue;

                    if(type == 1 && chunk.getBlock(x,y,z) != ChunkGen.BlockType.WATER.getID())
                        continue;

                    BlockModel model = Client.modelLoader.getModel(Client.modelPaths[chunk.getBlock(x,y,z)]);
                    for(Element element : model.getElements()) {
                        Map<String, Face> faces = element.getFaces();
                        for(Map.Entry<String, Face> face : faces.entrySet()) {
                            if(chunk.getBlock(x,y,z) == ChunkGen.BlockType.AIR.getID())
                                continue;

                            int FaceID = face.getValue().getCullFace();
                            if(FaceID == -1) // Check if there's a face
                                continue;

                            if(shouldRenderFace(xx, yy, zz, element, x,y,z, FaceID) == 0)
                                continue;

                            // Position index
                            if(type == 0)
                                buffer.put(ChunkGen.index(x, y, z, ChunkGen.CHUNK_SIZE));
                            else {
                                buffer.put(x);
                                buffer.put(y);
                                buffer.put(z);
                            }

                            // Minimum AABB
                            buffer.put(element.getFrom(0));
                            buffer.put(element.getFrom(1));
                            buffer.put(element.getFrom(2));
                            //Maximum AABB
                            buffer.put(element.getTo(0));
                            buffer.put(element.getTo(1));
                            buffer.put(element.getTo(2));

                            //Texture Index
                            buffer.put(ChunkGen.getTextureID(model, face));
                            // Face ID
                            buffer.put((float)FaceID);

                            // Ambient Occlusion
                            boolean canAmbientOcclusion = model.getAO();
                            if(type == 0) {
                                buffer.put(AO(FaceID, canAmbientOcclusion,
                                        x + (int)(xx * ChunkGen.CHUNK_SIZE),
                                        y + (int)(yy * ChunkGen.CHUNK_SIZE),
                                        z + (int)(zz * ChunkGen.CHUNK_SIZE)
                                ));
                            }
                        }
                    }
                }
            }
        }
        buffer.flip();


        return buffer;
    }

    private static float AmbientOcclusion(boolean side1, boolean side2, boolean corner) {
        if(side1 & side2)
            return 0.5f;

        return 1.0f - (0.25f * (side1? 1:0) + 0.25f * (side2? 1:0) + 0.25f * (corner? 1:0));
    }

    private static boolean isBlockSolid(int x, int y, int z) {
        float chunkX = floor((float) x / ChunkGen.CHUNK_SIZE);
        float chunkY = floor((float) y / ChunkGen.CHUNK_SIZE);
        float chunkZ = floor((float) z / ChunkGen.CHUNK_SIZE);

        float localX = floor((x % ChunkGen.CHUNK_SIZE + ChunkGen.CHUNK_SIZE) % ChunkGen.CHUNK_SIZE);
        float localY = floor((y % ChunkGen.CHUNK_SIZE + ChunkGen.CHUNK_SIZE) % ChunkGen.CHUNK_SIZE);
        float localZ = floor((z % ChunkGen.CHUNK_SIZE + ChunkGen.CHUNK_SIZE) % ChunkGen.CHUNK_SIZE);

        Chunk chunk = loadedChunks.get(new Vector3f(chunkX, chunkY, chunkZ));

        // If the chunk exists or is empty
        if(chunk == null || chunk.blocks == null)
            return false;

        byte blockID = chunk.getBlock((int) localX, (int) localY, (int) localZ);

        return blockID != ChunkGen.BlockType.AIR.getID() && blockID != ChunkGen.BlockType.WATER.getID();
    }

    private static float[] AO(int faceID, boolean canAO, int x, int y, int z) {
        float[] data;

        if(!canAO) {
            return new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
        }

        boolean top = false;
        boolean left = false;
        boolean right = false;
        boolean bottom = false;

        boolean cornerTopLeft = false;
        boolean cornerTopRight = false;
        boolean cornerBottomLeft = false;
        boolean cornerBottomRight = false;

        switch(faceID) {
            case 0:
                top = isBlockSolid(x, y + 1, z + 1);
                left = isBlockSolid(x - 1, y, z + 1);
                right = isBlockSolid(x + 1, y, z + 1);
                bottom = isBlockSolid(x, y - 1, z + 1);

                cornerTopLeft = isBlockSolid(x - 1, y + 1, z + 1);
                cornerTopRight = isBlockSolid(x + 1, y + 1, z + 1);
                cornerBottomLeft = isBlockSolid(x - 1, y - 1, z + 1);
                cornerBottomRight = isBlockSolid(x + 1, y - 1, z + 1);
                break;
            case 1:
                top = isBlockSolid(x, y + 1, z - 1);
                left = isBlockSolid(x + 1, y, z - 1);
                right = isBlockSolid(x - 1, y, z - 1);
                bottom = isBlockSolid(x, y - 1, z - 1);

                cornerTopLeft = isBlockSolid(x + 1, y + 1, z - 1);
                cornerTopRight = isBlockSolid(x - 1, y + 1, z - 1);
                cornerBottomLeft = isBlockSolid(x + 1, y - 1, z - 1);
                cornerBottomRight = isBlockSolid(x - 1, y - 1, z - 1);
                break;
            case 2:
                top = isBlockSolid(x - 1, y + 1, z);
                left = isBlockSolid(x - 1, y, z - 1);
                right = isBlockSolid(x - 1, y, z + 1);
                bottom = isBlockSolid(x - 1, y - 1, z);

                cornerTopLeft = isBlockSolid(x - 1, y + 1, z - 1);
                cornerTopRight = isBlockSolid(x - 1, y + 1, z + 1);
                cornerBottomLeft = isBlockSolid(x - 1, y - 1, z - 1);
                cornerBottomRight = isBlockSolid(x - 1, y - 1, z + 1);
                break;
            case 3:
                top = isBlockSolid(x + 1, y + 1, z);
                left = isBlockSolid(x + 1, y, z + 1);
                right = isBlockSolid(x + 1, y, z - 1);
                bottom = isBlockSolid(x + 1, y - 1, z);

                cornerTopLeft = isBlockSolid(x + 1, y + 1, z + 1);
                cornerTopRight = isBlockSolid(x + 1, y + 1, z - 1);
                cornerBottomLeft = isBlockSolid(x + 1, y - 1, z + 1);
                cornerBottomRight = isBlockSolid(x + 1, y - 1, z - 1);
                break;
            case 4:
                top = isBlockSolid(x, y + 1, z - 1);
                left = isBlockSolid(x - 1, y + 1, z);
                right = isBlockSolid(x + 1, y + 1, z);
                bottom = isBlockSolid(x, y + 1, z + 1);

                cornerTopLeft = isBlockSolid(x - 1,y + 1, z - 1);
                cornerTopRight = isBlockSolid(x + 1,y + 1, z - 1);
                cornerBottomLeft = isBlockSolid(x - 1,y + 1, z + 1);
                cornerBottomRight = isBlockSolid(x + 1,y + 1, z + 1);
                break;
            case 5:
                top = isBlockSolid(x, y - 1, z + 1);
                left = isBlockSolid(x - 1, y - 1, z);
                right = isBlockSolid(x + 1, y - 1, z);
                bottom = isBlockSolid(x, y - 1, z - 1);

                cornerTopLeft = isBlockSolid(x - 1, y - 1, z + 1);
                cornerTopRight = isBlockSolid(x + 1, y - 1, z + 1);
                cornerBottomLeft = isBlockSolid(x - 1, y - 1, z - 1);
                cornerBottomRight = isBlockSolid(x + 1, y - 1, z - 1);
                break;
        }

        data = new float[] {
                AmbientOcclusion(top, left, cornerTopLeft),
                AmbientOcclusion(top, right, cornerTopRight),
                AmbientOcclusion(bottom, left, cornerBottomLeft),
                AmbientOcclusion(bottom, right, cornerBottomRight)
        };
        return data;
    }
    public void onUpdate() {

        UpdateCameraFrustum();

        // Look at the name Damian...
        addChunksToQueue(false);

        // Yup, the name
        loadChunks();

        // it may be both clear and unclear
        ResolveChunkRender();
    }

    public void UpdateCameraFrustum() {
        // Takes the view Frustum
        frustumPlanes = Camera.getFrustumPlanes();

        // Get the Frsutum AABB
        min = ChunkGen.getLocalChunk(Camera.getFrustumMin());
        max = ChunkGen.getLocalChunk(Camera.getFrustumMax());

    }

    public void ResolveChunkRender() {

        long chunkX = (long) ChunkGen.getLocalChunk(player.position).x;
        long chunkY = (long) ChunkGen.getLocalChunk(player.position).y;
        long chunkZ = (long) ChunkGen.getLocalChunk(player.position).z;
        int radiusXYZ = Client.renderDistance / 2;

        List<Chunk> chunksToProcess = new ArrayList<>();

        // Multithreading pour déterminer les chunks à supprimer
        List<Future<List<Chunk>>> futures = new ArrayList<>();
        List<Chunk> chunkList = new ArrayList<>(loadedChunks.values());
        int chunkSize = chunkList.size() / MultiThreading.CHUNK_THREADS;

        for (int i = 0; i < MultiThreading.CHUNK_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i == MultiThreading.CHUNK_THREADS - 1) ? chunkList.size() : (i + 1) * chunkSize;

            List<Chunk> sublist = chunkList.subList(start, end);
            futures.add(MultiThreading.submitChunkTask(() -> {
                List<Chunk> localChunksToRemove = new ArrayList<>();
                for (Chunk chunk : sublist) {
                    if (chunk == null) continue;

                    if(chunk.blockDrawn == 0)
                        chunk.blocks = null;

                    long chunkDistX = abs(chunk.positionX - chunkX);
                    long chunkDistY = abs(chunk.positionY - chunkY);
                    long chunkDistZ = abs(chunk.positionZ - chunkZ);
                    if (chunkDistX > radiusXYZ || chunkDistZ > radiusXYZ || chunkDistY > radiusXYZ) {
                        localChunksToRemove.add(chunk);
                    }
                }
                return localChunksToRemove;
            }));
        }

        // Collecter les résultats des threads
        for (Future<List<Chunk>> future : futures) {
            try {
                chunksToProcess.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Ajouter les chunks à la file d'attente pour suppression dans le thread OpenGL
        for (int i = 0; i < Math.min(MAX_CHUNKS_TO_REMOVE_PER_FRAME, chunksToProcess.size()); i++) {
            chunkDeletionQueue.add(chunksToProcess.get(i));
        }
    }

    public void processChunkDeletions() {
        while (!chunkDeletionQueue.isEmpty()) {

            for(int i = 0; i < MAX_CHUNKS_TO_REMOVE_PER_FRAME; i++) {
                Chunk chunk = chunkDeletionQueue.poll();
                if(chunk == null) break;

                chunk.Delete();
                Vector3f chunkPos = new Vector3f(chunk.positionX, chunk.positionY, chunk.positionZ);
                loadedChunks.remove(chunkPos);
                loadedChunksID.remove(chunkPos);
            }
        }
    }

    public void renderChunks() {

        processChunkDeletions();

        // Create the list of chunks to render
        List<Chunk> chunksToRender = new ArrayList<>();

        // Multithreading to determine the chunks to render
        List<Future<List<Chunk>>> futures = new ArrayList<>();
        List<Vector3f> chunkList = new ArrayList<>(loadedChunksID.values());
        int chunkSize = chunkList.size() / MultiThreading.CHUNK_THREADS;

        for (int i = 0; i < MultiThreading.CHUNK_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i == MultiThreading.CHUNK_THREADS - 1) ? chunkList.size() : (i + 1) * chunkSize;

            List<Vector3f> sublist = chunkList.subList(start, end);
            futures.add(MultiThreading.submitChunkTask(() -> {
                List<Chunk> localChunksToRender = new ArrayList<>();
                for (Vector3f chunkID : sublist) {

                    Chunk chunk = loadedChunks.get(chunkID);
                    if (chunk == null) continue;

                    if(!ChunkGen.isChunkInFrustum(frustumPlanes,
                            chunk.positionX * ChunkGen.CHUNK_SIZE,
                            chunk.positionY * ChunkGen.CHUNK_SIZE,
                            chunk.positionZ * ChunkGen.CHUNK_SIZE))
                        continue;

                    localChunksToRender.add(chunk);
                }

                return localChunksToRender;
            }));
        }

        // Collect threads results
        for (Future<List<Chunk>> future : futures) {
            try {
                chunksToRender.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < Client.blockTextures.length; i++) {
            Client.blockTextures[i].Bind(i);
        }

        // Depth render
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glPolygonMode(GL_FRONT_AND_BACK, Debugger.Rendering);

        // Enable BackFace Culling
        glCullFace(GL_FRONT);
        glFrontFace(GL_CW);

        Vector3f fogColor = (WorldEnvironment.isUnderWater)?
                new Vector3f(0.14f, 0.21f, 0.42f) :
                WorldEnvironment.interpolateFogColor(player.position.y);

        ChunkShaders[0].Bind();
        ChunkShaders[0].Uniform1iv("u_Textures", Client.samplers);
        ChunkShaders[0].Uniform1i("AmbientOcclusion", (Debugger.AmbientOcclusion)? 1:0);

        fogColor = (fogColor.x > WorldEnvironment.SURFACE_DEFAULT_COLOR.x)? WorldEnvironment.SURFACE_DEFAULT_COLOR : fogColor;

        ChunkShaders[0].Uniform3f("fogColor", fogColor);
        ChunkShaders[0].Uniform1f("fogDistance", WorldEnvironment.fogDistance);
        ChunkShaders[0].Uniform1i("UnderWater", (WorldEnvironment.isUnderWater)?1:0);

        ChunkShaders[0].Uniform1f("renderDistance", Client.renderDistance);
        ChunkShaders[0].Uniform3f("cameraPos", new Vector3f(Camera.Position));
        ChunkShaders[0].UniformMatrix4x4("view", new Matrix4f(Camera.GetViewMatrix()));
        ChunkShaders[0].UniformMatrix4x4("projection", new Matrix4f(Camera.GetProjectionMatrix()));

        // Draw static blocks
        glDisable(GL_BLEND);
        glEnable(GL_CULL_FACE);

        long i = 0;

        for(Chunk chunk : chunksToRender) {
            if(chunk.StaticBlocks == null) continue;
            ChunkShaders[0].Uniform3f("Position", chunk.positionX, chunk.positionY, chunk.positionZ);
            chunk.DrawMesh();
            i++;
        }
        ChunkDrawCalls = i;

        ChunkShaders[1].Bind();
        ChunkShaders[1].Uniform1iv("u_Textures", Client.samplers);

        ChunkShaders[1].Uniform3f("fogColor", fogColor);
        ChunkShaders[1].Uniform1f("fogDistance", WorldEnvironment.fogDistance);

        ChunkShaders[1].Uniform1i("UnderWater", (WorldEnvironment.isUnderWater)?1:0);
        ChunkShaders[1].Uniform1f("Time", (float) glfwGetTime());
        ChunkShaders[1].Uniform1f("renderDistance", Client.renderDistance);
        ChunkShaders[1].Uniform3f("cameraPos", new Vector3f(Camera.Position));
        ChunkShaders[1].UniformMatrix4x4("view", new Matrix4f(Camera.GetViewMatrix()));
        ChunkShaders[1].UniformMatrix4x4("projection", new Matrix4f(Camera.GetProjectionMatrix()));

        // Draw liquid blocks
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        for(Chunk chunk : chunksToRender) {
            if(chunk.LiquidBlocks == null) continue;

            ChunkShaders[1].Uniform3f("Position", chunk.positionX, chunk.positionY, chunk.positionZ);
            chunk.DrawLiquidMesh();
        }
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    protected static int shouldRenderFace(long xx, long yy, long zz, Element element, int x, int y, int z, int face) {
        Chunk actualChunk = loadedChunks.get(new Vector3f(xx, yy, zz));

        int nx = x, ny = y, nz = z;

        // 0 -- FRONT, 1 -- BACK, 2 -- RIGHT, 3 -- LEFT, 4 -- TOP, 5 -- BOTTOM
        switch (face) {
            case 0:   nz += 1; break; // +Z
            case 1:   nz -= 1; break; // -Z
            case 2:   nx -= 1; break; // -X
            case 3:   nx += 1; break; // +X
            case 4:   ny += 1; break; // +Y
            case 5:   ny -= 1; break; // -Y
        }

        // Vérifier si le chunk actuel est valide et contient un bloc
        if (actualChunk == null || actualChunk.getBlock(x,y,z) == ChunkGen.BlockType.AIR.getID()) {
            return 0;
        }

        // Vérifier si on sort du chunk actuel
        if (nx < 0 || nx >= ChunkGen.CHUNK_SIZE || ny < 0 || ny >= ChunkGen.CHUNK_SIZE || nz < 0 || nz >= ChunkGen.CHUNK_SIZE) {

            // Determine the neighbor chunk
            long neighborChunkX = xx + (nx < 0 ? -1 : (nx >= ChunkGen.CHUNK_SIZE ? 1 : 0));
            long neighborChunkY = yy + (ny < 0 ? -1 : (ny >= ChunkGen.CHUNK_SIZE ? 1 : 0));
            long neighborChunkZ = zz + (nz < 0 ? -1 : (nz >= ChunkGen.CHUNK_SIZE ? 1 : 0));

            // Set nx et nz in neighbor local space
            nx = (nx + ChunkGen.CHUNK_SIZE) % ChunkGen.CHUNK_SIZE;
            ny = (ny + ChunkGen.CHUNK_SIZE) % ChunkGen.CHUNK_SIZE;
            nz = (nz + ChunkGen.CHUNK_SIZE) % ChunkGen.CHUNK_SIZE;

            // Load the neighborChunk
            Chunk neighborChunk = loadedChunks.get(new Vector3f(neighborChunkX,neighborChunkY,neighborChunkZ));

            if (neighborChunk == null || neighborChunk.blocks == null || neighborChunk.getBlock(nx,ny,nz) == ChunkGen.BlockType.AIR.getID()) {
                return 1;
            }

            // Vérifier l'opacité du bloc adjacent du chunk adjacent
            BlockModel nextBlock = Client.modelLoader.getModel(Client.modelPaths[neighborChunk.getBlock(nx,ny,nz)]);
            Element nextBlockFirstElement = nextBlock.getElements().getFirst();
            if (!nextBlockFirstElement.isOpacity()) {
                return element.isOpacity() ? 1 : 0;
            }

            return 0;
        }

        // Check if the neighbor is air
        if (actualChunk.getBlock(nx,ny,nz) == ChunkGen.BlockType.AIR.getID()) {
            return 1;
        }

        // Vérifier l'opacité du bloc adjacent
        BlockModel nextBlock = Client.modelLoader.getModel(Client.modelPaths[actualChunk.getBlock(nx,ny,nz)]);
        Element nextBlockFirstElement = nextBlock.getElements().getFirst();
        if (!nextBlockFirstElement.isOpacity()) {
            return element.isOpacity() ? 1 : 0;
        }

        return 0;
    }


    public void renderEntities() {

        // Depth render
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        glDisable(GL_CULL_FACE);

        Vector3f fogColor = (WorldEnvironment.isUnderWater)?
                new Vector3f(0.14f, 0.21f, 0.42f) :
                WorldEnvironment.interpolateFogColor(player.position.y);
        EntitiesShader.Bind();

        if(fogColor.x > WorldEnvironment.SURFACE_DEFAULT_COLOR.x)
            EntitiesShader.Uniform3f("fogColor", WorldEnvironment.SURFACE_DEFAULT_COLOR);
        else EntitiesShader.Uniform3f("fogColor", fogColor);
        EntitiesShader.Uniform1f("fogDistance", WorldEnvironment.fogDistance);
        EntitiesShader.Uniform1i("UnderWater", (WorldEnvironment.isUnderWater)?1:0);

        EntitiesShader.Uniform1f("renderDistance", Client.renderDistance);
        EntitiesShader.Uniform3f("cameraPos", new Vector3f(player.position));
        EntitiesShader.UniformMatrix4x4("view", new Matrix4f(Camera.GetViewMatrix()));
        EntitiesShader.UniformMatrix4x4("projection", new Matrix4f(Camera.GetProjectionMatrix()));

        for(Entity entity : entities) {
            entity.Draw(EntitiesShader);
        }
    }

    public void addCollision(CubeCollision collision) {
        worldCollisions.add(collision);
    }

    public ArrayList<CubeCollision> getCollisions() {
        return worldCollisions;
    }

    public CubeCollision getCollisions(int index) {
        return worldCollisions.get(index);
    }

    public void renderCollisions(Matrix4f view, Matrix4f projection) {
        glDisable(GL_BLEND);
        glDisable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        shader.Bind();
        shader.UniformMatrix4x4("view", view);
        shader.UniformMatrix4x4("projection", projection);

        for(CubeCollision collision : worldCollisions) {
            Matrix4d model = new Matrix4d().identity()
                    .translate(collision.position)
                    .scale(new Vector3d(collision.size));
            shader.UniformMatrix4x4("model", new Matrix4f(model));
            shader.Uniform1f("borderThickness", 1);
            collision.drawAABB();
        }
    }
}
