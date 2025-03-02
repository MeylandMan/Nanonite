package net.GameLayer;

import net.Core.*;
import net.Core.Physics.CubeCollision;
import net.Core.Rendering.Shader;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.joml.Math.*;

public class World {
    public static ArrayList<CubeCollision> worldCollisions;
    public Shader shader;
    public static final float GRAVITY = 0;
    static double[] chunkRenderSpeed = new double[2];
    static double[] chunkQueueSpeed = new double[2];

    // Chunks datas
    public static boolean loadChunks = true;
    static Queue<Chunk> chunks;
    private static Chunk[][] loadedChunks;


    public static float genPlayerPosX;
    public static float genPlayerPosZ;

    public World() {
        worldCollisions = new ArrayList<>();
        shader = new Shader();
        shader.CreateShader("Physics.vert", "Physics.frag");
    }

    public static void addChunksToQueue(Camera camera, boolean reset) {
        if(!loadChunks){
            if(chunkQueueSpeed[1] == 0) {
                chunkQueueSpeed[1] = glfwGetTime();
                Logger.log(Logger.Level.INFO, "Chunk queue filled in " +
                        String.format("%.3f", (chunkQueueSpeed[1] - chunkQueueSpeed[0])) +
                        " seconds");
                Logger.log(Logger.Level.INFO, "Rendering chunks...");
                chunkQueueSpeed[0] = glfwGetTime();
                chunkRenderSpeed[0] = glfwGetTime();
            }

            return;
        }

        chunkQueueSpeed[0] = chunkQueueSpeed[1] = 0;
        chunkQueueSpeed[0] = glfwGetTime();

        if(loadedChunks != null && reset) {
            for (Chunk[] loadedChunk : loadedChunks) {
                for(Chunk chunk : loadedChunk) {
                    if(chunk == null) {
                        System.out.println("Skipped chunk row");
                        continue;
                    }

                    chunk.Delete();
                }
            }
            loadedChunks = null;
            chunks = null;
        }

        Logger.log(Logger.Level.INFO, "Loading chunks...");

        if(loadedChunks == null) {
            int size = (Client.renderDistance*2)-1;
            loadedChunks = new Chunk[size][size];
            chunks = new LinkedList<>();
        }

        genPlayerPosX = camera.Position.x;
        genPlayerPosZ = camera.Position.z;

        int chunkX = (int) (genPlayerPosX / ChunkGen.X_DIMENSION);
        int chunkZ = (int) (genPlayerPosZ / ChunkGen.Z_DIMENSION);
        int radius = Client.renderDistance / 2;

        for(int dz = -radius; dz <= radius; dz++) {
            for(int dx = -radius; dx <= radius; dx++) {
                int worldX = (chunkX + dx) * ChunkGen.X_DIMENSION;
                int worldZ = (chunkZ + dz) * ChunkGen.Z_DIMENSION;

                chunks.add(new Chunk(new Vector2f(worldX, worldZ), (byte)dx, (byte)dz));
            }
        }

        loadChunks = false;
    }

    public void loadChunks(Camera camera) {
        if(chunks.isEmpty()) {
            if(chunkRenderSpeed[1] != 0) {
                Logger.log(Logger.Level.INFO, "Rendered chunks in " +
                        String.format("%.3f", (chunkRenderSpeed[1] - chunkRenderSpeed[0])) +
                        " seconds");
                chunkRenderSpeed[0] = chunkRenderSpeed[1] = 0;
            }

            int radius = Client.renderDistance / 2;
            for(Chunk[] loadChunk : loadedChunks) {
                for(Chunk chunk : loadChunk) {
                    if(chunk == null) continue;


                    if(chunk.updateChunk) {
                        int x = chunk.dx + radius;
                        int z = chunk.dz + radius;
                        chunk.updateChunk(x, z);
                        if(x == loadedChunks.length-1 && z == loadedChunks.length-1 ) {
                            System.out.println("Done");
                        }
                        break;
                    }
                }
            }
            return;
        }

        chunkRenderSpeed[1] = glfwGetTime();

        for(int i = 0; i < Client.renderDistance / 2; i++) {
            Chunk queuedChunk = chunks.poll();
            if(queuedChunk == null)
                continue;

            int radius = Client.renderDistance / 2;
            int x = queuedChunk.dx + radius;
            int z = queuedChunk.dz + radius;

            loadedChunks[x][z] = queuedChunk;
            ChunkGen.setupChunk(loadedChunks[x][z]);
        }
    }

    protected static FloatBuffer getChunkData(int xx, int zz) {
        Chunk chunk = loadedChunks[xx][zz];

        // 3 positions + 3 min + 3 max + 1 texID + 1 FaceID
        int estimatedSizeBuffer = ChunkGen.getBlocks(chunk) * 11;
        FloatBuffer buffer = BufferUtils.createFloatBuffer(estimatedSizeBuffer);


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
        addChunksToQueue(camera, true);
        loadChunks(camera);
    }


    public void renderChunks(Shader shader) {
        if(loadedChunks == null)
            return;

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

        for(Chunk[] _loadedChunks : loadedChunks) {

            for(Chunk chunk : _loadedChunks) {
                if(chunk == null)
                    continue;
                shader.Uniform3f("Position", chunk.positionX, chunk.positionY, chunk.positionZ);
                chunk.DrawMesh();
            }
        }
    }

    protected static int shouldRenderFace(int xx, int zz, Element element, int x, int y, int z, int face) {
        Chunk actualChunk = loadedChunks[xx][zz];

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
            if (cnx < 0 || cnx >= loadedChunks.length || cnz < 0 || cnz >= loadedChunks[0].length) {
                return 0;
            } else {
                Chunk nextChunk = loadedChunks[cnx][cnz];
                if(nextChunk != null) {
                    int nnx = 0, nnz = 0;
                    if(nx < 0)
                        nnx = ChunkGen.X_DIMENSION-1;
                    if(nz < 0)
                        nz = ChunkGen.Z_DIMENSION-1;

                    ChunkGen.BlockType nextBlock = nextChunk.blocks[nnx][y][nnz];

                    if(nextBlock == null) {
                        return 1;
                    }
                }
                return 0;
            }
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
