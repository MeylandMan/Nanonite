import GameLayer.Rendering.*;
import GameLayer._Object;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static org.lwjgl.opengl.GL43.*;

public class Chunk extends _Object {

    public final static byte X_DIMENSION = 16;
    public final static short Y_DIMENSION = 255;
    public final static byte Z_DIMENSION = 16;
    int Y_MAX = 5;
    boolean update = true;

    private final Block[][][] blocks = new Block[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];

    VBO ssbo;

    int blockdrawn = 0;
    int facedrawn = 0;

    String[] texture_paths = {
            "blocks/dirt.png",
            "blocks/grass_block_side.png",
            "blocks/grass_block_top.png",
    };
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

        // Fill void with Air and check if a
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    if(blocks[x][y][z] == null)
                        blocks[x][y][z] = new Block(new Vector3f(x, y, z));

                    if(blocks[x][y][z].type == Block.BlockType.DIRT) {
                        if(blocks[x][y+1][z].type == Block.BlockType.AIR)
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
    }

    private int shouldRenderFace(int x, int y, int z, @NotNull Block.Faces face) {
        int nx = x, ny = y, nz = z;

        switch (face) {
            case RIGHT:  nx -= 1; break; // -X
            case LEFT:   nx += 1; break; // +X
            case FRONT:  nz -= 1; break; // -Z
            case BACK:   nz += 1; break; // +Z
            case BOTTOM: ny -= 1; break; // -Y
            case TOP:    ny += 1; break; // +Y
        }

        // Check if the current block is not AIR or VOID
        if(blocks[x][y][z].ID == -1) {
            return 0;
        }

        // Vérifier si la face est en bordure du chunk
        if (nx < 0 || nx >= X_DIMENSION || ny < 0 || ny >= Y_DIMENSION || nz < 0 || nz >= Z_DIMENSION) {
            return 1; // Bordure -> Afficher la face
        }

        // Vérifier si le bloc adjacent est de type AIR
        if(blocks[nx][ny][nz].opacity == 0)
            return 1;

        return 0;
    }

    @Override
    public void DrawMesh(Shader shader) {
        /*


        for(int i = 0; i < textures.length; i++) {


        }
        */
        int[] samplers = new int[textures.length];
        for(int i = 0; i < textures.length; i++) {
            samplers[i] = i;
            textures[i].Bind(i);
        }

        blockdrawn = 0;
        facedrawn = 0;

        int estimatedSize = X_DIMENSION * Y_DIMENSION * Z_DIMENSION * 4; // 3 position + 1 ID + 1 Opacity
        FloatBuffer buffer = MemoryUtil.memAllocFloat(estimatedSize);
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {

                    int is_block_rendered = shouldRenderFace(x,y,z, Block.Faces.FRONT)+
                            shouldRenderFace(x,y,z, Block.Faces.BACK)+
                            shouldRenderFace(x,y,z, Block.Faces.RIGHT)+
                            shouldRenderFace(x,y,z, Block.Faces.LEFT)+
                            shouldRenderFace(x,y,z, Block.Faces.TOP)+
                            shouldRenderFace(x,y,z, Block.Faces.BOTTOM);

                    if(blocks[x][y][z].ID != -1 && is_block_rendered != 0) {
                        blockdrawn++;
                        buffer.put(blocks[x][y][z].getPosition().x);
                        buffer.put(blocks[x][y][z].getPosition().y);
                        buffer.put(blocks[x][y][z].getPosition().z);
                        buffer.put(blocks[x][y][z].ID);
                        //buffer.put(blocks[x][y][z].opacity);

                        facedrawn = (shouldRenderFace(x,y,z, Block.Faces.FRONT) == 1)? facedrawn+1 : facedrawn;
                        facedrawn = (shouldRenderFace(x,y,z, Block.Faces.BACK) == 1)? facedrawn+1 : facedrawn;
                        facedrawn = (shouldRenderFace(x,y,z, Block.Faces.RIGHT) == 1)? facedrawn+1 : facedrawn;
                        facedrawn = (shouldRenderFace(x,y,z, Block.Faces.LEFT) == 1)? facedrawn+1 : facedrawn;
                        facedrawn = (shouldRenderFace(x,y,z, Block.Faces.TOP) == 1)? facedrawn+1 : facedrawn;
                        facedrawn = (shouldRenderFace(x,y,z, Block.Faces.BOTTOM) == 1)? facedrawn+1 : facedrawn;
                    }

                    //int index = x + (y * X_DIMENSION) + (z * X_DIMENSION * Y_DIMENSION);

                }
            }
        }
        buffer.flip();

        ssbo.Bind();
        ssbo.SubData(0, buffer);
        ssbo.BindBase(0);

        shader.Uniform1iv("u_Textures", samplers);
        shader.Uniform3f("Position", positionX, positionY, positionZ);

        glDrawArrays(GL_TRIANGLES, 0, 6*facedrawn);
    }

    public void InitTextures() {
        if (!GL.getCapabilities().OpenGL30) {
            throw new IllegalStateException("OpenGL 3.0 unavailable !");
        }
        ssbo = new VBO(GL_DYNAMIC_DRAW, GL_SHADER_STORAGE_BUFFER);
        ssbo.InitSSBO(
                X_DIMENSION*Y_DIMENSION*Z_DIMENSION * (4*Float.BYTES + 2*Integer.BYTES),
                0);
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
