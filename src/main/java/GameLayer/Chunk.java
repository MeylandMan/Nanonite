package GameLayer;

import GameLayer.Rendering.Scene;
import org.joml.Vector3f;

public class Chunk {
    private final Vector3f position;
    private final Block[][][] blocks = new Block[16][5][16];
    public Chunk(Scene scene) {
        this.position = new Vector3f();
        setupChunk(scene);
    }

    public Chunk(Scene scene, Vector3f position) {
        this.position = position;
        setupChunk(scene);
    }

    private void setupChunk(Scene scene) {
        for(int x = 0; x < 16; x++) {
            for(int y = 0; y < 5; y++) {
                for(int z = 0; z < 16; z++) {
                    blocks[x][y][z] = new Block("dirt.png",
                            new Vector3f(
                                    this.position.x+x,
                                    this.position.y+y,
                                    this.position.z+z
                            ));
                    blocks[x][y][z].AddToScene(scene);
                }
            }
        }
    }

    public Vector3f getPosition() {
        return position;
    }
}
