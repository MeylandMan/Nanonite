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

    private final float positionX;
    private final float positionZ;
    private final Block[][][] blocks = new Block[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];

    protected int[] indices = {};
    protected float[] vertices = {};
    String[] texture_paths = {};

    VAO ChunkVAO;
    VBO ChunkVBO;
    EBO ChunkEBO;
    public Texture[] textures;

    public Chunk(Scene scene, Vector3f position) {
        //Add Air
        this.positionX = position.x;
        this.positionZ = position.z;
        for(int x = 0; x < X_DIMENSION; x++) {
            for(int y = 0; y < Y_DIMENSION; y++) {
                for(int z = 0; z < Z_DIMENSION; z++) {
                    blocks[x][y][z] = new Block( new Vector3f(
                                    this.positionX+x,
                                        y,
                                    this.positionZ+z
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
                    if(y == 4 && z == 2) {
                        continue;
                    }
                    blocks[x][y][z] = new Block( new Vector3f(
                                    this.positionX+x,
                                        y,
                                    this.positionZ+z
                            ));

                    blocks[x][y][z].type = ( y == 4 )? Block.BlockType.GRASS : Block.BlockType.DIRT;

                    //Check if we should draw faces
                    if (shouldRenderFace(x, y, z, Block.Faces.FRONT)) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.FRONT);
                        BlockData.createFaceIndices(this, Block.Faces.FRONT);
                    }
                    if (shouldRenderFace(x, y, z, Block.Faces.BACK)) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.BACK);
                        BlockData.createFaceIndices(this, Block.Faces.BACK);
                    }
                    if (shouldRenderFace(x, y, z, Block.Faces.RIGHT)) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.RIGHT);
                        BlockData.createFaceIndices(this, Block.Faces.RIGHT);
                    }
                    if (shouldRenderFace(x, y, z, Block.Faces.LEFT)) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.LEFT);
                        BlockData.createFaceIndices(this, Block.Faces.LEFT);
                    }
                    if (shouldRenderFace(x, y, z, Block.Faces.BOTTOM)) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.BOTTOM);
                        BlockData.createFaceIndices(this, Block.Faces.BOTTOM);
                    }
                    if (shouldRenderFace(x, y, z, Block.Faces.TOP)) {
                        BlockData.createFaceVertices(this, blocks[x][y][z], Block.Faces.TOP);
                        BlockData.createFaceIndices(this, Block.Faces.TOP);
                    }
                }
            }
        }
        Init();
        AddToScene(scene);
    }

    private boolean shouldRenderFace(int x, int y, int z, Block.Faces face) {
        int nx = x, ny = y, nz = z;

        switch (face) {
            case FRONT:  nz += 1; break;
            case BACK:   nz -= 1; break;
            case RIGHT:  nx += 1; break;
            case LEFT:   nx -= 1; break;
            case TOP:    ny += 1; break;
            case BOTTOM: ny -= 1; break;
        }

        // Vérifier si la face est en bordure du chunk
        if (nx < 0 || nx >= X_DIMENSION || ny < 0 || ny >= Y_DIMENSION || nz < 0 || nz >= Z_DIMENSION) {
            return true; // Bordure -> Afficher la face
        }

        // Vérifier si le bloc adjacent est de type AIR
        return (blocks[nx][ny][nz].type == Block.BlockType.AIR);
    }

    public void AddToScene(Scene scene) {
        scene.AddChunk(this);
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(new Vector3f(positionX, 0, positionZ))                 // Translation
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
        return new Vector3f(positionX, 0, positionZ);
    }
}
