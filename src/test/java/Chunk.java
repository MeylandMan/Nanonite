import GameLayer.Rendering.*;
import GameLayer._Object;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL43.*;

public class Chunk extends _Object {

    public final static byte X_DIMENSION = 16;
    public final static short Y_DIMENSION = 255;
    public final static byte Z_DIMENSION = 16;
    int Y_MAX = 5;
    boolean updateChunk = true;
    private FloatBuffer buffer;

    private final Block[][][] blocks = new Block[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];
    VBO ssbo;
    int facedrawn = 0;
    String[] texture_paths = {
            "blocks/dirt.png",
            "blocks/grass_block_side.png",
            "blocks/grass_block_top.png"
    };
    int[] samplers = new int[texture_paths.length];

    public Texture[] textures;

    public Chunk(Scene scene, @NotNull Vector3f position) {
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
                    blocks[x][y][z] = new Block( new Vector3f(x, y, z));
                    Block.BlockType type =  ( y == Y_MAX-1 )? Block.BlockType.GRASS : Block.BlockType.DIRT;
                    blocks[x][y][z].setType(type);
                }
            }
        }

        // check if threre's blocks at the top of the dirt
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    if(blocks[x][y][z] == null)
                        continue;
                    if(blocks[x][y][z].type == Block.BlockType.DIRT) {
                        if(blocks[x][y+1][z] == null)
                            blocks[x][y][z].setType(Block.BlockType.GRASS);
                    }
                }
            }
        }

        InitTextures();
        AddToScene(scene);

    }

    @Override
    public void Delete() {
        for(Texture texture : textures)
            texture.Delete();
        MemoryUtil.memFree(buffer);
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
                    for(int i = 0; i < 6; i++) {
                        if(blocks[x][y][z].ID != -1 && shouldRenderFace(x,y,z, i) == 1) {
                            buffer.put(blocks[x][y][z].getPosition().x);
                            buffer.put(blocks[x][y][z].getPosition().y);
                            buffer.put(blocks[x][y][z].getPosition().z);
                            buffer.put(0.0f);
                            int id = (i == 4 && blocks[x][y][z].ID == 1)? blocks[x][y][z].ID+1 : blocks[x][y][z].ID;
                            buffer.put((float)id);
                            buffer.put((float)i);
                            buffer.put(0.0f);
                            buffer.put(0.0f);
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

    private int shouldRenderFace(int x, int y, int z, int face) {
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
        if(blocks[nx][ny][nz].opacity == 0)
            return 1;

        return 0;
    }

    @Override
    public void DrawMesh(Shader shader) {

        for(int i = 0; i < textures.length; i++) {
            samplers[i] = i;
            textures[i].Bind(i);
        }
        if(updateChunk) {
            updateMesh();
            updateChunk = false;
        }

        ssbo.BindBase(0);
        shader.Uniform1iv("u_Textures", samplers);
        shader.Uniform3f("Position", positionX, positionY, positionZ);

        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT);
        glDrawArrays(GL_TRIANGLES, 0, 6*facedrawn);
    }

    public void InitTextures() {
        if (!GL.getCapabilities().OpenGL30) {
            throw new IllegalStateException("OpenGL 3.0 unavailable !");
        }

        ssbo = new VBO(GL_DYNAMIC_DRAW, GL_SHADER_STORAGE_BUFFER);
        int estimatedSize = ((X_DIMENSION*Y_DIMENSION*Z_DIMENSION)/2) * 6 * (8*Float.BYTES);
        ssbo.InitSSBO(Math.round(estimatedSize*0.6f),0);

        textures = new Texture[texture_paths.length];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = new Texture(texture_paths[i]);
        }
    }

    public Block GetBlock(int x) {
        return blocks[x][0][0];
    }
    public Block GetBlock(int x, int y) {
        return blocks[x][y][0];
    }
    public Block GetBlock(Vector2f position) {
        return blocks[(int)position.x][(int)position.y][0];
    }
    public Block GetBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }
    public Block GetBlock(Vector3f position) {
        return blocks[(int)position.x][(int)position.y][(int)position.z];
    }
}
