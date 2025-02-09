package GameLayer;

import GameLayer.Rendering.Scene;
import GameLayer.Rendering.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL11.*;

public class Chunk {

    final static int X_DIMENSION = 16;
    final static int Y_DIMENSION = 16;
    final static int Z_DIMENSION = 16;
    private final Vector3f position;
    private final Block[][][] blocks = new Block[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];

    private int[] indices = {};
    private float[] vertices = {};
    String[] texture_paths = {};

    VAO ChunkVAO;
    VBO ChunkVBO;
    EBO ChunkEBO;
    public Texture texture;

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
                    blocks[x][y][z] = new Block("dirt.png",
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
                    blocks[x][y][z] = new Block("dirt.png",
                            new Vector3f(
                                    this.position.x+x,
                                    this.position.y+y,
                                    this.position.z+z
                            ));

                    if(z == 0)
                        blocks[x][y][z].addDataToVertice(Block.Faces.FRONT);
                    if(z == Z_DIMENSION-1)
                        blocks[x][y][z].addDataToVertice(Block.Faces.BACK);



                    if(x == 0)
                        blocks[x][y][z].addDataToVertice(Block.Faces.RIGHT);
                    if(x == X_DIMENSION-1)
                        blocks[x][y][z].addDataToVertice(Block.Faces.LEFT);

                    if(y == 0)
                        blocks[x][y][z].addDataToVertice(Block.Faces.BOTTOM);
                    if(y == 4)
                        blocks[x][y][z].addDataToVertice(Block.Faces.TOP);

                    int prev_size = vertices.length;
                    float[] prev_v_data = vertices;
                    vertices = new float[prev_size+blocks[x][y][z].vertices.length];
                    System.arraycopy(prev_v_data, 0, vertices, 0, prev_v_data.length);

                    for(int i = 0; i < blocks[x][y][z].vertices.length; i++) {
                        vertices[prev_size+i] = blocks[x][y][z].vertices[i];
                    }

                    prev_size = indices.length;
                    int[] prev_i_data = indices;
                    indices = new int[prev_size+blocks[x][y][z].indices.length];
                    System.arraycopy(prev_i_data, 0, indices, 0, prev_i_data.length);

                    int last;
                    if(indices[1] == 0)
                        last = Block.findMax(indices);
                    else
                        last = Block.findMax(indices)+1;

                    for(int i = 0; i < blocks[x][y][z].indices.length; i++) {
                        indices[prev_size+i] = blocks[x][y][z].indices[i] + last;
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

    public void DrawMesh() {
        texture.Bind();
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
        texture = new Texture("dirt.png");

        // Initialize them
        ChunkVBO.Init(vertices);
        layout.Add(3);
        layout.Add(2);
        layout.Add(3);
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
