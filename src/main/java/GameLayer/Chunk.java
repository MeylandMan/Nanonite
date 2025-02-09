package GameLayer;

import GameLayer.Rendering.Scene;
import org.joml.Vector3f;

public class Chunk {

    final static int X_DIMENSION = 16;
    final static int Y_DIMENSION = 16;
    final static int Z_DIMENSION = 16;
    private final Vector3f position;
    private final Block[][][] blocks = new Block[X_DIMENSION][Y_DIMENSION][Z_DIMENSION];
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

                    blocks[x][y][z].addDataToVertice(Block.Faces.BACK);

                    if(x == 0)
                        blocks[x][y][z].addDataToVertice(Block.Faces.RIGHT);
                    if(x == X_DIMENSION-1)
                        blocks[x][y][z].addDataToVertice(Block.Faces.LEFT);

                    if(y == 0)
                        blocks[x][y][z].addDataToVertice(Block.Faces.BOTTOM);
                    blocks[x][y][z].addDataToVertice(Block.Faces.TOP);




                    blocks[x][y][z].createDatas();
                    blocks[x][y][z].mesh.Init();
                    blocks[x][y][z].AddToScene(scene);
                }
            }
        }
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
