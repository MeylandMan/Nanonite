package GameLayer;

import GameLayer.Physics.CubeCollision;
import GameLayer.Rendering.Camera;

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
        for(CubeCollision collision : worldCollisions) {
            if(collision == camera.collision)
                continue;
            if(collision.intersects(camera.collision)) {
                CubeCollision.resolveCollision(collision, camera.collision, deltaTime);
                camera.updateCameraVectors();
            }
        }
    }

    public void onRender() {
        for(CubeCollision collision : worldCollisions) {
            collision.drawAABB();
        }
    }
}
