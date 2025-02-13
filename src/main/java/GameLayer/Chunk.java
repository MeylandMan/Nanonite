package GameLayer;

import GameLayer.Rendering.Scene;
import GameLayer.Rendering.*;
import org.jetbrains.annotations.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class Chunk extends _Object{

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

        // Fill with AIR
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    blocks[x][y][z] = new Block( new Vector3f(x, y, z));
                }
            }
        }
        setupChunk(scene);
    }

    private void setupChunk(Scene scene) {
        ArrayList<Byte> vertices = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

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
            for(int y = 0; y < Y_MAX; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    if(blocks[x][y][z].type == Block.BlockType.DIRT) {
                        if(blocks[x][y+1][z].type == Block.BlockType.AIR)
                            blocks[x][y][z].type = Block.BlockType.GRASS;
                    }
                }
            }
        }

        // Check if we can add faces
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    if(blocks[x][y][z].type == Block.BlockType.AIR)
                        continue;
                    renderBlock(vertices, indices, x, y, z);
                }
            }
        }

        Init(vertices, indices);
        AddToScene(scene);

    }
    public void renderBlock(ArrayList<Byte> vertices, ArrayList<Integer> indices, int x, int y, int z) {
        if (shouldRenderFace(x, y, z, Block.Faces.FRONT)) {
            BlockData.createFaceVertices(vertices, blocks[x][y][z], Block.Faces.FRONT);
            BlockData.createFaceIndices(indices, Block.Faces.FRONT);
        }
        if (shouldRenderFace(x, y, z, Block.Faces.BACK)) {
            BlockData.createFaceVertices(vertices, blocks[x][y][z], Block.Faces.BACK);
            BlockData.createFaceIndices(indices, Block.Faces.BACK);
        }
        if (shouldRenderFace(x, y, z, Block.Faces.RIGHT)) {
            BlockData.createFaceVertices(vertices, blocks[x][y][z], Block.Faces.RIGHT);
            BlockData.createFaceIndices(indices, Block.Faces.RIGHT);
        }
        if (shouldRenderFace(x, y, z, Block.Faces.LEFT)) {
            BlockData.createFaceVertices(vertices, blocks[x][y][z], Block.Faces.LEFT);
            BlockData.createFaceIndices(indices, Block.Faces.LEFT);
        }
        if (shouldRenderFace(x, y, z, Block.Faces.BOTTOM)) {
            BlockData.createFaceVertices(vertices, blocks[x][y][z], Block.Faces.BOTTOM);
            BlockData.createFaceIndices(indices, Block.Faces.BOTTOM);
        }
        if (shouldRenderFace(x, y, z, Block.Faces.TOP)) {
            BlockData.createFaceVertices(vertices, blocks[x][y][z], Block.Faces.TOP);
            BlockData.createFaceIndices(indices, Block.Faces.TOP);
        }
        length = indices.size();
    }

    private boolean shouldRenderFace(int x, int y, int z, @NotNull Block.Faces face) {
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
        if(blocks[x][y][z].type == Block.BlockType.AIR) {
            return false;
        }

        // Vérifier si la face est en bordure du chunk
        if (nx < 0 || nx >= X_DIMENSION || ny < 0 || ny >= Y_DIMENSION || nz < 0 || nz >= Z_DIMENSION) {
            return true; // Bordure -> Afficher la face
        }

        // Vérifier si le bloc adjacent est de type AIR
        return (blocks[nx][ny][nz].type == Block.BlockType.AIR);
    }

    @Override
    public void Delete() {
        for(Texture texture : textures)
            texture.Delete();
        vao.Delete();
        vbo.Delete();
        ebo.Delete();
    }

    @Override
    public void DrawMesh(Shader shader) {

        int[] samplers = new int[textures.length];
        for(int i = 0; i < textures.length; i++) {
            samplers[i] = i;
            textures[i].Bind(i);
        }
        shader.Uniform1iv("u_Textures", samplers);

        vao.Bind();
        ebo.Bind();

        glDrawElements(GL_TRIANGLES, length, GL_UNSIGNED_INT, 0);

        vao.UnBind();
        ebo.UnBind();

        for (Texture texture : textures) {
            texture.Unbind();
        }
    }

    @Override
    public void Init(ArrayList<Byte> vertices, ArrayList<Integer> indices) {
        if (!GL.getCapabilities().OpenGL30) {
            throw new IllegalStateException("OpenGL 3.0 unavailable !");
        }

        vao = new VAO();
        vbo = new VBO(GL_DYNAMIC_DRAW);
        ebo = new EBO(GL_DYNAMIC_DRAW);

        VertexBufferLayout layout = new VertexBufferLayout();
        textures = new Texture[TEXTURE_LOADED];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = new Texture(BlockData.getTexturePath(i));
        }

        // Initialize them
        //vbo.Init(4 * 9 * (X_DIMENSION * Y_DIMENSION * Z_DIMENSION));
        vbo.Init(vertices);
        layout.AddBytes(3);
        layout.AddBytes(2);
        layout.AddBytes(3);
        layout.AddBytes(1);
        vao.AddBuffer(vbo, layout);

        ebo.Init(indices);
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
