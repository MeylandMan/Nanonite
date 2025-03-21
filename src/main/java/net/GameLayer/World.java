package net.GameLayer;

import net.Core.*;
import net.Core.Physics.CubeCollision;
import net.Core.Rendering.Shader;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.sudoplay.joise.module.ModuleBasisFunction;

import static org.joml.Math.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;

public class World {
    public static ArrayList<CubeCollision> worldCollisions;
    public Shader shader;
    public static final float GRAVITY = 0;

    // Procedural generation datas
    static ModuleBasisFunction basis;
    public static long seed;

    // Chunks datas
    public static boolean allowQuery = true;
    public static boolean firstLoad = true;
    public static boolean isAllRendered = false;
    public static final Map<Vector2f, Chunk> loadedChunks = new HashMap<>();
    // File des chunks à supprimer dans le thread OpenGL
    private static ConcurrentLinkedQueue<Chunk> chunkDeletionQueue = new ConcurrentLinkedQueue<>();

    private static final int CHUNK_THREADS = Client.MAX_THREADS / 2;

    public static Camera.Plane[] frustumPlanes;

    public static boolean renderQuery = false;
    static double[] chunkRenderSpeed = new double[2];
    static double[] chunkQueueSpeed = new double[2];

    // Chunk Queues
    private static PriorityQueue<ChunkDistance> chunksPos;
    private static Queue<Chunk> chunksToRemove = new LinkedList<>();

    // Macros
    private static final int MAX_CHUNKS_TO_REMOVE_PER_FRAME = 2;

    private static class ChunkDistance {
        Chunk chunk;
        float distance;

        ChunkDistance(Chunk chunk, float distance) {
            this.chunk = chunk;
            this.distance = distance;
        }
    }

    public World() {
        worldCollisions = new ArrayList<>();
        chunksPos = new PriorityQueue<>(Comparator.comparingDouble(c -> c.distance));
        shader = new Shader();
        shader.CreateShader("Physics.vert", "Physics.frag");

        Random rand = new Random();
        seed = rand.nextLong();
        basis = new ModuleBasisFunction();
        basis.setType(ModuleBasisFunction.BasisType.SIMPLEX);
        basis.setSeed(seed);
    }

    public World(long sd) {
        worldCollisions = new ArrayList<>();
        chunksPos = new PriorityQueue<>(Comparator.comparingDouble(c -> c.distance));
        shader = new Shader();
        shader.CreateShader("Physics.vert", "Physics.frag");
        seed = sd;
        basis = new ModuleBasisFunction();
        basis.setType(ModuleBasisFunction.BasisType.SIMPLEX);
        basis.setSeed(seed);
    }

    public static void addChunksToQueue(Camera camera, boolean reset) {

        chunkQueueSpeed[0] = glfwGetTime();

        if(reset) {

            chunkDeletionQueue.addAll(loadedChunks.values());
            loadedChunks.clear();
            chunksPos.clear();

            Logger.log(Logger.Level.INFO, "Deleted previous chunks");
            Logger.log(Logger.Level.INFO, "Loading chunks...");
        }


        long chunkX = (long) (Camera.Position.x / ChunkGen.X_DIMENSION);
        long chunkZ = (long) (Camera.Position.z / ChunkGen.Z_DIMENSION);
        int radius = Client.renderDistance / 2;
        isAllRendered = false;

        // Takes the view Frustum
        frustumPlanes = camera.getFrustumPlanes();

        for (int dz = -radius; dz <= radius; dz++) {
            for (int dx = -radius; dx <= radius; dx++) {
                long worldX = (chunkX + dx);
                long worldZ = (chunkZ + dz);

                if (!ChunkGen.isChunkInFrustum(frustumPlanes, worldX * ChunkGen.X_DIMENSION,
                        worldZ * ChunkGen.Z_DIMENSION)) continue;

                Vector2f chunkID = new Vector2f(worldX, worldZ);
                if (loadedChunks.containsKey(chunkID)) continue;

                Chunk chunk = new Chunk(worldX, worldZ);

                // Utilisation de la distance au carré pour éviter sqrt()
                float distanceSquared = dx * dx + dz * dz;
                chunksPos.add(new ChunkDistance(chunk, distanceSquared));

                ChunkGen.setupChunk(chunk);
                loadedChunks.put(chunkID, chunk);
            }
        }

        if(reset) {
            chunkQueueSpeed[1] = glfwGetTime();
            double result = chunkQueueSpeed[1] - chunkQueueSpeed[0];
            Logger.log(Logger.Level.INFO, "Chunk queue filled in " + String.format("%.3f", result) + "s");
            Logger.log(Logger.Level.INFO, "Rendering chunks...");
        }
        allowQuery = reset;
    }


    public static void updateNearbyChunks(Vector2f v) {
        Vector2f[] neighbors = {
                new Vector2f(v.x + 1, v.y),
                new Vector2f(v.x - 1, v.y),
                new Vector2f(v.x, v.y + 1),
                new Vector2f(v.x, v.y - 1)
        };

        for (Vector2f neighbor : neighbors) {
            Chunk neighborChunk = loadedChunks.get(neighbor);
            if (neighborChunk != null) {

                 if (neighborChunk.Ssbo == null) neighborChunk.Init();
                neighborChunk.updateChunk = true;
                neighborChunk.updateChunk((int) neighbor.x, (int) neighbor.y);
            }
        }
    }

    public void loadChunks() {
        if (chunksPos.isEmpty()) {
            if (renderQuery) {
                chunkRenderSpeed[1] = glfwGetTime();
                double result = Math.abs(chunkQueueSpeed[1] - chunkRenderSpeed[0]);
                Logger.log(Logger.Level.INFO, "Rendered chunks in " + String.format("%.3f", result) + "s");
                renderQuery = false;
            }
            isAllRendered = true;
            chunksToRemove.clear();
            return;
        }

        renderQuery = allowQuery;
        chunkRenderSpeed[0] = glfwGetTime();

        int maxChunksPerFrame = Client.renderDistance / 2;
        for (int i = 0; i < maxChunksPerFrame && !chunksPos.isEmpty(); i++) {
            Chunk getID = chunksPos.poll().chunk;
            if (getID == null) break;

            Vector2f chunkID = new Vector2f(getID.positionX, getID.positionZ);
            Chunk chunk = loadedChunks.get(chunkID);

            if (chunk == null) continue;
            if (chunk.Ssbo == null) chunk.Init();

            updateNearbyChunks(chunkID);
            chunk.updateChunk((long) chunkID.x, (long) chunkID.y);
        }
    }

    protected static FloatBuffer getChunkData(long xx, long zz) {
        Vector2f chunkID = new Vector2f(xx, zz);
        Chunk chunk = loadedChunks.get(chunkID);

        // 3 positions + 3 min + 3 max + 1 texID + 1 FaceID
        int estimatedSizeBuffer = (ChunkGen.getBlocksNumber(chunk) * 6) * 11;
        FloatBuffer buffer = MemoryUtil.memAllocFloat(estimatedSizeBuffer);


        for(int x = 0; x < ChunkGen.X_DIMENSION; x++) {
            for(int y = 0; y < ChunkGen.Y_DIMENSION; y++) {
                for(int z = 0; z < ChunkGen.Z_DIMENSION; z++) {
                    if(chunk.blocks[x][y][z] == null)
                        continue;

                    BlockModel model = Client.modelLoader.getModel(Client.modelPaths[chunk.blocks[x][y][z].getID()]);
                    for(Element element : model.getElements()) {
                        Map<String, Face> faces = element.getFaces();
                        for(Map.Entry<String, Face> face : faces.entrySet()) {
                            if(chunk.blocks[x][y][z] == null)
                                continue;

                            int FaceID = face.getValue().getCullFace();
                            if(FaceID == -1) // Check if there's a face
                                continue;

                            if(shouldRenderFace(xx, zz, element, x,y,z, FaceID) == 0)
                                continue;

                            // Position
                            buffer.put(x);
                            buffer.put(y);
                            buffer.put(z);
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
                        }
                    }
                }
            }
        }
        buffer.flip();


        return buffer;
    }

    public void onUpdate(Camera camera, float deltaTime) {
        if(firstLoad) {
            firstLoad = false;
            // Load chunks to the queue
            addChunksToQueue(camera, true);
        }
        addChunksToQueue(camera, false);

        // Load chunks
        loadChunks();
        //Check if the surrounding chunks are still worthy of staying in the array
        ResolveChunkRender(camera, deltaTime);
    }

    public void ResolveChunkRender(Camera camera, float deltaTime) {
        if (!isAllRendered) return;

        long chunkX = (long) (Camera.Position.x / ChunkGen.X_DIMENSION);
        long chunkZ = (long) (Camera.Position.z / ChunkGen.Z_DIMENSION);
        float radius = Client.renderDistance / 2.f;

        List<Chunk> chunksToProcess = new ArrayList<>();

        // Multithreading pour déterminer les chunks à supprimer
        List<Future<List<Chunk>>> futures = new ArrayList<>();
        List<Chunk> chunkList = new ArrayList<>(loadedChunks.values());
        int chunkSize = chunkList.size() / CHUNK_THREADS;

        for (int i = 0; i < CHUNK_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i == CHUNK_THREADS - 1) ? chunkList.size() : (i + 1) * chunkSize;

            List<Chunk> sublist = chunkList.subList(start, end);
            futures.add(MultiThreading.submitTask(() -> {
                List<Chunk> localChunksToRemove = new ArrayList<>();
                for (Chunk chunk : sublist) {
                    if (chunk == null) continue;
                    long chunkDistX = Math.abs(chunk.positionX - chunkX);
                    long chunkDistZ = Math.abs(chunk.positionZ - chunkZ);
                    if (chunkDistX > radius || chunkDistZ > radius) {
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
            Chunk chunk = chunkDeletionQueue.poll();
            if (chunk != null) {
                chunk.Delete(); // Exécute OpenGL dans le thread principal
                Vector2f chunkPos = new Vector2f(chunk.positionX,chunk.positionZ);
                loadedChunks.remove(chunkPos);
            }
        }
    }

    public void renderChunks(Shader shader) {

        processChunkDeletions();

        for(int i = 0; i < Client.blockTextures.length; i++) {
            Client.blockTextures[i].Bind(i);
        }

        glDisable(GL_BLEND);

        // Depth render
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // Enable BackFace Culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glFrontFace(GL_CW);

        shader.Uniform1iv("u_Textures", Client.samplers);

        for(Chunk chunk : loadedChunks.values()) {

            if(chunk.Ssbo == null || !ChunkGen.isChunkInFrustum(frustumPlanes, chunk.positionX * ChunkGen.X_DIMENSION,
                    chunk.positionZ * ChunkGen.Z_DIMENSION))
                continue;

            shader.Uniform3f("Position", chunk.positionX, chunk.positionY, chunk.positionZ);
            chunk.DrawMesh();
        }
    }

    protected static int shouldRenderFace(long xx, long zz, Element element, int x, int y, int z, int face) {
        Chunk actualChunk = loadedChunks.get(new Vector2f(xx, zz));

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
        if (actualChunk == null || actualChunk.blocks[x][y][z] == null) {
            return 0;
        }

        // Vérifier si on est en dehors des limites Y
        if (ny < 0 || ny >= ChunkGen.Y_DIMENSION) {
            return 1;
        }

        // Vérifier si on sort du chunk actuel
        if (nx < 0 || nx >= ChunkGen.X_DIMENSION || nz < 0 || nz >= ChunkGen.Z_DIMENSION) {
            // Déterminer le chunk voisin
            long neighborChunkX = xx + (nx < 0 ? -1 : (nx >= ChunkGen.X_DIMENSION ? 1 : 0));
            long neighborChunkZ = zz + (nz < 0 ? -1 : (nz >= ChunkGen.Z_DIMENSION ? 1 : 0));

            // Set nx et nz in neighbor local space
            nx = (nx + ChunkGen.X_DIMENSION) % ChunkGen.X_DIMENSION;
            nz = (nz + ChunkGen.Z_DIMENSION) % ChunkGen.Z_DIMENSION;

            // Load the neighborChunk
            Chunk neighborChunk = loadedChunks.get(new Vector2f(neighborChunkX, neighborChunkZ));
            if (neighborChunk == null || neighborChunk.blocks[nx][ny][nz] == null) {
                return 1;
            }
        } else {
            // Check the actual chunk
            if (actualChunk.blocks[nx][ny][nz] == null) {
                return 1;
            }
        }

        // Vérifier l'opacité du bloc adjacent
        if (!element.isOpacity()) {
            return 1;
        }

        return 0;
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
