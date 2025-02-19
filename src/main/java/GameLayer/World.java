package GameLayer;

import Core.Physics.CubeCollision;
import Core.Rendering.Camera;

import java.util.ArrayList;

public class World {
    public static ArrayList<CubeCollision> worldCollisions;

    public static final float GRAVITY = 0;

    public World() {
        worldCollisions = new ArrayList<>();
    }

    public void addCollision(CubeCollision collision) {
        worldCollisions.add(collision);
    }

    public ArrayList<CubeCollision> getCollisions() {
        return worldCollisions;
    }

    public CubeCollision getCollisions(int index) {
        return worldCollisions.get(index);
    }

    public void onUpdate(Camera camera, float deltaTime) {

    }

    public void onRender() {
        for(CubeCollision collision : worldCollisions) {
            collision.drawAABB();
        }
    }
}
