package GameLayer;

import GameLayer.Rendering.Scene;
import GameLayer.Rendering.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class Chunk {

    final static int X_DIMENSION = 16;
    final static int Y_DIMENSION = 255;
    final static int Z_DIMENSION = 16;
    final static int TEXTURE_LOADED = 3;
    private final Vector3f position;
    private final Block[][][] blocks = new Block[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];

    protected int[] indices = {};
    protected float[] vertices = {};
    String[] texture_paths = {};

    VAO ChunkVAO;
    VBO ChunkVBO;
    EBO ChunkEBO;
    public Texture[] textures;

    public Chunk(Scene scene) {
        this.position = new Vector3f();

        setupChunk(scene);
    }

    public Chunk(Scene scene, Vector3f position) {
        //Add Air
        this.position = position;
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    blocks[x][y][z] = new Block("blocks/dirt.png",
                            new Vector3f(
                                    this.position.x+x,
                                    this.position.y+y,
                                    this.position.z+z
                            ));
                    blocks[x][y][z].type = Block.BlockType.AIR;
                }
            }
        }
        setupChunk(scene);
    }

    private void setupChunk(Scene scene) {
        // Add Blocks
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < 5; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    blocks[x][y][z] = new Block("blocks/dirt.png",
                            new Vector3f(
                                    this.position.x+x,
                                    this.position.y+y,
                                    this.position.z+z
                            ));

                    blocks[x][y][z].type = Block.BlockType.DIRT;
                    if(y == 4) {
                        blocks[x][y][z].type = Block.BlockType.GRASS;
                    }


                    if(z == 0){
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.FRONT);
                        BlockData.createFaceIndices(this, Block.Faces.FRONT);
                    }

                    if(z == Z_DIMENSION-1) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.BACK);
                        BlockData.createFaceIndices(this, Block.Faces.BACK);
                    }

                    if(x == 0) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.RIGHT);
                        BlockData.createFaceIndices(this, Block.Faces.RIGHT);
                    }

                    if(x == X_DIMENSION-1) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.LEFT);
                        BlockData.createFaceIndices(this, Block.Faces.LEFT);
                    }


                    if(y == 0) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.BOTTOM);
                        BlockData.createFaceIndices(this, Block.Faces.BOTTOM);
                    }

                    if(y == 4) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.TOP);
                        BlockData.createFaceIndices(this, Block.Faces.TOP);
                    }
                }
            }
        }
        Init();
        AddToScene(scene);
    }

    public void AddToScene(Scene scene) {
        scene.AddChunk(this);
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(position)                 // Translation
                .rotateXYZ(new Vector3f())                 // Rotation
                .scale(new Vector3f(1.0f));                       // Scale
    }

    public void Delete() {
        for(Texture texture : textures)
            texture.Delete();
        ChunkVAO.Delete();
        ChunkVBO.Delete();
        ChunkEBO.Delete();
    }
    public void DrawMesh(Shader shader) {
        int[] samplers = new int[textures.length];
        for(int i = 0; i < textures.length; i++) {
            samplers[i] = i;
            textures[i].Bind(i);
        }
        shader.Uniform1iv("u_Textures", samplers);
        ChunkVAO.Bind();
        ChunkEBO.Bind();


        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

        ChunkVAO.UnBind();
        ChunkEBO.UnBind();
    }

    public void Init() {
        if (!GL.getCapabilities().OpenGL30) {
            throw new IllegalStateException("OpenGL 3.0 non disponible !");
        }

        ChunkVAO = new VAO();
        ChunkVBO = new VBO();
        ChunkEBO = new EBO();

        VertexBufferLayout layout = new VertexBufferLayout();
        textures = new Texture[TEXTURE_LOADED];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = new Texture(BlockData.getTexturePath(i));
        }


        // Initialize them
        ChunkVBO.Init(vertices);
        layout.Add(3);
        layout.Add(2);
        layout.Add(3);
        layout.Add(1);
        ChunkVAO.AddBuffer(ChunkVBO, layout);

        ChunkEBO.Init(indices);
    }
    public Block GetBlock(int x) {
        return blocks[x][0][0];
    }
    public Block GetBlock(int x, int y) {
        return blocks[x][y][0];
    }
    public Block GetBlock(int x, int y, int z) {
        return blocks[x][y][z];
    }
    public Vector3f getPosition() {
        return position;
    }
}
