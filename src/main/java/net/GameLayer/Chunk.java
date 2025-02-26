package net.GameLayer;

import net.Core.*;
import net.Core.Rendering.Scene;
import net.Core.Rendering.Shader;
import net.Core.Rendering.VBO;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL43.*;
import static net.GameLayer.ChunkGen.*;


public class Chunk extends _Object {
    boolean updateChunk = true;
    private FloatBuffer buffer;

    private final BlockType[][][] blocks = new BlockType[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];
    VBO ssbo;
    int facedrawn = 0;

    int[] samplers = new int[Client.blockTexturePath.size()];




    public Chunk(Scene scene, Vector3f position) {
        this.positionX = (int)position.x;
        this.positionZ = (int)position.z;

        setupChunk(scene);
    }

    private void setupChunk(Scene scene) {

        // Add Blocks inside the chunk
        int zz = 2;
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_MAX; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    blocks[x][y][z] = (y == Y_MAX-1)? BlockType.GRASS : BlockType.DIRT;
                }
            }
        }

        // check if threre's blocks at the top of the dirt
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    if(blocks[x][y][z] == null)
                        continue;
                    if(blocks[x][y][z] == BlockType.DIRT) {
                        if(blocks[x][y+1][z] == null)
                            blocks[x][y][z] = BlockType.GRASS;
                    }
                }
            }
        }

        InitTextures();
        AddToScene(scene);

    }

    @Override
    public void Delete() {

    }


    private void updateMesh() {
        facedrawn = 0;
        int estimatedSizeBuffer = (X_DIMENSION * Y_DIMENSION * Z_DIMENSION)/2 * 6 * 8; // 3 position + 1 ID + 1 Face + 2 padding
        buffer = MemoryUtil.memAllocFloat(estimatedSizeBuffer);

        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    if(blocks[x][y][z] == null)
                        continue;

                    BlockModel model = Client.modelLoader.getModel("blocks/"+blocks[x][y][z].getName());
                    for(Element element : model.getElements()) {
                        Map<String, Face> faces = element.getFaces();
                        for(Map.Entry<String, Face> face : faces.entrySet()) {
                            if(blocks[x][y][z] == null)
                                continue;

                            int FaceID = face.getValue().getCullFace();
                            if(shouldRenderFace(element, x,y,z, FaceID) == 0)
                                continue;


                            String textureName = face.getValue().getTexture();

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
                            buffer.put(getTextureID(model, face));
                            // Face ID
                            buffer.put((float)FaceID);
                            facedrawn++;
                        }
                    }
                }
            }
        }
        buffer.flip();

        ssbo.Bind();
        ssbo.SubData(0, buffer);
        ssbo.UnBind();
        MemoryUtil.memFree(buffer);
    }

    private float getTextureID(BlockModel model, Map.Entry<String, Face> face) {

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

    private int shouldRenderFace(Element element, int x, int y, int z, int face) {
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

        // Check if the current block is not AIR or VOID
        if(blocks[x][y][z] == null) {
            return 0;
        }

        // Vérifier si la face est en bordure du chunk
        if (nx < 0 || nx >= X_DIMENSION || ny < 0 || ny >= Y_DIMENSION || nz < 0 || nz >= Z_DIMENSION) {
            return 1; // Bordure -> Afficher la face
        }

        if(blocks[nx][ny][nz] == null)
            return 1;

        // Vérifier si le bloc adjacent est de type AIR

        if(!element.isOpacity())
           return 1;

        return 0;
    }

    @Override
    public void DrawMesh(Shader shader) {

        for(int i = 0; i < Client.blockTextures.length; i++) {
            samplers[i] = i;
            Client.blockTextures[i].Bind(i);
        }
        if(updateChunk) {
            updateMesh();
            updateChunk = false;
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

        ssbo.BindBase(0);
        shader.Uniform1iv("u_Textures", samplers);
        shader.Uniform3f("Position", positionX, positionY, positionZ);

        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        glDrawArrays(GL_TRIANGLES, 0, 6*facedrawn);
    }

    public void InitTextures() {
        if (!GL.getCapabilities().OpenGL30) {
            Logger.log(Logger.Level.ERROR, "OpenGL 4.3 not supported");
        }

        ssbo = new VBO(GL_DYNAMIC_DRAW, GL_SHADER_STORAGE_BUFFER);
        int estimatedSize = ((X_DIMENSION*Y_DIMENSION*Z_DIMENSION)/2) * 6 * (8*Float.BYTES);
        ssbo.InitSSBO(Math.round(estimatedSize*0.6f),0);
    }

}
