import GameLayer.Rendering.*;
import GameLayer._Object;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class Chunk extends _Object {

    public final static int X_DIMENSION = 16;
    public final static int Y_DIMENSION = 255;
    public final static int Z_DIMENSION = 16;
    final static int TEXTURE_LOADED = 3;
    int Y_MAX = 5;
    int temp = 0;

    private final Block[][][] blocks = new Block[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];

    
    String[] texture_paths = {};
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
                    blocks[x][y][z].type = ( y == Y_MAX-1 )? Block.BlockType.GRASS : Block.BlockType.DIRT;
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
                            blocks[x][y][z].type = Block.BlockType.GRASS;
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
        shader.Uniform3f("Position", positionX, positionY, positionZ);
        glDrawArrays(GL_TRIANGLES, 0, 36*16*255*16);
    }

    public void InitTextures() {
        if (!GL.getCapabilities().OpenGL30) {
            throw new IllegalStateException("OpenGL 3.0 unavailable !");
        }
        textures = new Texture[TEXTURE_LOADED];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = new Texture(BlockData.getTexturePath(i));
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
