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

import static org.lwjgl.opengl.GL43.*;

public class Chunk extends _Object {

    public final static int X_DIMENSION = 16;
    public final static int Y_DIMENSION = 255;
    public final static int Z_DIMENSION = 16;
    int Y_MAX = 5;
    boolean update = true;

    private final Block[][][] blocks = new Block[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];
    VBO ssbo;

    int blockdrawn = 0;

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

    @Override
    public void DrawMesh(Shader shader) {
        /*

        int[] samplers = new int[textures.length];
        for(int i = 0; i < textures.length; i++) {
            samplers[i] = i;

        }
        */

        blockdrawn = 0;
        int estimatedSize = X_DIMENSION * Y_DIMENSION * Z_DIMENSION * 4; // 3 position + 1 ID
        FloatBuffer buffer = BufferUtils.createFloatBuffer(estimatedSize);
        IntBuffer opacity = BufferUtils.createIntBuffer(X_DIMENSION * Y_DIMENSION * Z_DIMENSION);
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {

                    if(blocks[x][y][z].ID != -1) {
                        blockdrawn++;
                        buffer.put(blocks[x][y][z].getPosition().x);
                        buffer.put(blocks[x][y][z].getPosition().y);
                        buffer.put(blocks[x][y][z].getPosition().z);
                        buffer.put(blocks[x][y][z].ID);
                    }

                    int index = x + (y * X_DIMENSION) + (z * X_DIMENSION * Y_DIMENSION);
                    opacity.put(blocks[x][y][z].opacity);

                }
            }
        }
        buffer.flip();
        opacity.flip();

        ssbo.Bind();
        ssbo.SubData(0, buffer);


        ssbo.BindBase(0);

        textures[0].Bind();

        shader.Uniform1iv("BlockOpacity", opacity);


        shader.Uniform1i("u_Texture", 0);
        shader.Uniform3f("Position", positionX, positionY, positionZ);

        glDrawArrays(GL_TRIANGLES, 0, 36*blockdrawn);

        MemoryUtil.memFree(buffer);
        MemoryUtil.memFree(opacity);
    }

    public void InitTextures() {
        if (!GL.getCapabilities().OpenGL30) {
            throw new IllegalStateException("OpenGL 3.0 unavailable !");
        }
        ssbo = new VBO(GL_DYNAMIC_DRAW, GL_SHADER_STORAGE_BUFFER);
        ssbo.InitSSBO(X_DIMENSION*Y_DIMENSION*Z_DIMENSION * (3*Float.BYTES + Integer.BYTES), 0);

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
