package net.GameLayer;

import net.Core.*;
import net.Core.Physics.CubeCollision;
import net.Core.Rendering.Shader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;

import static org.joml.Math.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;

public class World {
    public static ArrayList<CubeCollision> worldCollisions;
    public Shader shader;
    public static final float GRAVITY = 0;


    // Chunks datas
    public static boolean allowQuery = true;
    public static boolean firstLoad = true;
    public static boolean isAllDeleted = false;
    public static float delete_cooldown = 0;
    public static boolean isAllRendered = false;
    public static Map<Vector2f, Chunk> loadedChunks = new HashMap<>();

    public static boolean renderQuery = false;
    static double[] chunkRenderSpeed = new double[2];
    static double[] chunkQueueSpeed = new double[2];

    // Chunk Queues
    private static final Queue<Vector2f> chunksPos = new LinkedList<>();
    private static final Queue<Chunk> chunksToRemove = new LinkedList<>();

    // Macros
    private static final int MAX_CHUNKS_TO_REMOVE_PER_FRAME = 4;

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
        shader = new Shader();
        shader.CreateShader("Physics.vert", "Physics.frag");
    }

    public static void addChunksToQueue(Camera camera, boolean reset) {

        chunkQueueSpeed[0] = glfwGetTime();


        if(reset) {

            for(Chunk chunk : loadedChunks.values()) {
                chunk.Delete();
            }
            System.gc();

            loadedChunks.clear();
            chunksPos.clear();
            chunksToRemove.clear();

            Logger.log(Logger.Level.INFO, "Deleted previous chunks");

            Logger.log(Logger.Level.INFO, "Loading chunks...");
        }


        int chunkX = (int) (camera.Position.x / ChunkGen.X_DIMENSION);
        int chunkZ = (int) (camera.Position.z / ChunkGen.Z_DIMENSION);
        int radius = Client.renderDistance / 2;
        isAllRendered = false;

        // Temporary list of chunks with their distance to the player
        List<ChunkDistance> chunkList = new ArrayList<>();

        for (int dz = -radius; dz <= radius; dz++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int worldX = (chunkX + dx) * ChunkGen.X_DIMENSION;
                int worldZ = (chunkZ + dz) * ChunkGen.Z_DIMENSION;

                Vector2f position =  new Vector2f(worldX, worldZ);
                if(loadedChunks.containsKey(new Vector2f(worldX / ChunkGen.X_DIMENSION,
                        worldZ / ChunkGen.Z_DIMENSION))) continue;

                Chunk chunk = new Chunk(position);


                float distance = (float) Math.sqrt(dx * dx + dz * dz);
                chunkList.add(new ChunkDistance(chunk, distance));
            }
        }

        // Sort by the nearest to the farthest from the player
        chunkList.sort(Comparator.comparingDouble(c -> c.distance));

        for (ChunkDistance chunkDistance : chunkList) {
            Chunk chunk = chunkDistance.chunk;
            Vector2f chunkPos = new Vector2f(chunk.positionX, chunk.positionZ);
            Vector2f chunkID = new Vector2f(chunk.positionX / ChunkGen.X_DIMENSION,
                    chunk.positionZ / ChunkGen.Z_DIMENSION);
            chunksPos.add(chunkPos);

            ChunkGen.setupChunk(chunk);
            loadedChunks.put(chunkID, chunk);
        }

        if(reset) {
            chunkQueueSpeed[1] = glfwGetTime();
            double result = chunkQueueSpeed[1] - chunkQueueSpeed[0];
            Logger.log(Logger.Level.INFO, "Chunk queue filled in " + String.format("%.3f", result) + "s");
            Logger.log(Logger.Level.INFO, "Rendering chunks...");
        }
        allowQuery = reset;
        chunkList.clear();
    }

    public void loadChunks() {
        if(chunksPos.isEmpty()) {
            if(renderQuery) {
                chunkRenderSpeed[1] = glfwGetTime();
                double result = abs(chunkQueueSpeed[1] - chunkRenderSpeed[0]);
                Logger.log(Logger.Level.INFO, "Rendered chunks in " + String.format("%.3f", result) + "s");
                renderQuery = false;
            }
            isAllRendered = true;
            chunksToRemove.clear();
            return;
        }

        renderQuery = allowQuery;
        chunkRenderSpeed[0] = glfwGetTime();

        for(int i = 0; i < Client.renderDistance / 2; i++) {
            Vector2f queuedChunkPos = chunksPos.poll();
            if(queuedChunkPos == null)
                break;

            Vector2f chunkID = new Vector2f(queuedChunkPos.x / ChunkGen.X_DIMENSION,
                    queuedChunkPos.y / ChunkGen.Z_DIMENSION);

            Chunk chunk = loadedChunks.get(chunkID);
            if(chunk == null) continue;

            chunk.Init();
            chunk.updateChunk((int) chunkID.x, (int) chunkID.y);
        }
    }

    protected static FloatBuffer getChunkData(int xx, int zz) {
        Vector2f chunkID = new Vector2f(xx, zz);
        Chunk chunk = loadedChunks.get(chunkID);

        // 3 positions + 3 min + 3 max + 1 texID + 1 FaceID
        int estimatedSizeBuffer = ChunkGen.getBlocksNumber(chunk) * 11;
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

        // Load chunks
        loadChunks();
        //Check if the surrounding chunks are still worthy of staying in the array
        ResolveChunkRender(camera, deltaTime);
    }


    public void ResolveChunkRender(Camera camera, float deltaTime) {
        if (loadedChunks == null || !isAllRendered) return;

        int chunkX = (int) (camera.Position.x / ChunkGen.X_DIMENSION);
        int chunkZ = (int) (camera.Position.z / ChunkGen.Z_DIMENSION);
        float radius = Client.renderDistance / 2.f;

        // Put the out-of-reach chunks out of the box
        for (Chunk chunk : loadedChunks.values()) {
            if (chunk == null) continue;

            int chunkDistX = Math.abs((chunk.positionX / ChunkGen.X_DIMENSION) - chunkX);
            int chunkDistZ = Math.abs((chunk.positionZ / ChunkGen.X_DIMENSION) - chunkZ);

            if (chunkDistX > radius || chunkDistZ > radius) {
                chunksToRemove.add(chunk);
            }
        }

        // Deleting chunks and replacing deleted with new one
        for (int i = 0; i < MAX_CHUNKS_TO_REMOVE_PER_FRAME && !chunksToRemove.isEmpty(); i++) {
            Chunk chunk = chunksToRemove.poll();
            if(chunk == null) return;
            Vector2f chunkPos = new Vector2f(chunk.positionX / ChunkGen.X_DIMENSION,
                    chunk.positionZ / ChunkGen.Z_DIMENSION);
            chunk.Delete();
            loadedChunks.remove(chunkPos);
        }
        addChunksToQueue(camera, false);
    }

    public void renderChunks(Shader shader) {

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
            if(chunk.Ssbo == null) continue;

            shader.Uniform3f("Position", chunk.positionX, chunk.positionY, chunk.positionZ);
            chunk.DrawMesh();
        }
    }

    protected static int shouldRenderFace(int xx, int zz, Element element, int x, int y, int z, int face) {
        Chunk actualChunk = loadedChunks.get(new Vector2f(xx, zz));

        int nx = x, ny = y, nz = z;
        int cnx = xx, cnz = zz;

        // 0 -- FRONT, 1 -- BACK, 2 -- RIGHT, 3 -- LEFT, 4 -- TOP, 5 -- BOTTOM
        switch (face) {
            case 0:   nz += 1; cnz += 1; break; // +Z
            case 1:   nz -= 1; cnz -= 1; break; // -Z
            case 2:   nx -= 1; cnx -= 1; break; // -X
            case 3:   nx += 1; cnx += 1; break; // +X
            case 4:   ny += 1; break; // +Y
            case 5:   ny -= 1; break; // -Y
        }

        // Check if the current block is not AIR or VOID
        if(actualChunk == null || actualChunk.blocks[x][y][z] == null) {
            return 0;
        }

        // Check if the nearby chunk is out of length
        // Check if the face is in a Chunk's border
        if(nx < 0 || nx >= ChunkGen.X_DIMENSION || nz < 0 || nz >= ChunkGen.Z_DIMENSION || ny < 0 || ny >= ChunkGen.Y_DIMENSION) {
            //Check the next chunk
            Chunk nextChunk = loadedChunks.get(new Vector2f(cnx, cnz));
            if(nextChunk != null) {
                int nnx = 0, nnz = 0;
                if(nx < 0)
                    nnx = ChunkGen.X_DIMENSION-1;
                if(nz < 0)
                    nnz = ChunkGen.Z_DIMENSION-1;

                ChunkGen.BlockType nextBlock = nextChunk.blocks[nnx][y][nnz];

                if(nextBlock == null) {
                    return 1;
                }
            }
            return 0;
        }


        if(actualChunk.blocks[nx][ny][nz] == null)
            return 1;

        // Vérifier si le bloc adjacent est transparent
        if(!element.isOpacity())
            return 1;

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
            Matrix4f model = new Matrix4f().identity()
                    .translate(collision.position)
                    .scale(collision.size);
            shader.UniformMatrix4x4("model", model);
            shader.Uniform1f("borderThickness", 1);
            collision.drawAABB();
        }
    }
}
