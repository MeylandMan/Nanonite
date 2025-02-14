package GameLayer;

import GameLayer.Physics.CubeCollision;
import GameLayer.Rendering.Camera;

import java.util.ArrayList;

public class World {
    ArrayList<CubeCollision> collisions;
    public static final float GRAVITY = 0;

    public World() {
        collisions = new ArrayList<>();
    }

    public void addCollision(CubeCollision collision) {
        collisions.add(collision);
    }

    public ArrayList<CubeCollision> getCollisions() {
        return collisions;
    }

    public CubeCollision getCollisions(int index) {
        return collisions.get(index);
    }

    public void onUpdate(Camera camera, float deltaTime) {
        for(CubeCollision collision : collisions) {
            if(collision.intersects(camera.collision)) {
                CubeCollision.resolveCollision(collision, camera.collision, deltaTime);
            }
        }
    }

    public void onRender() {
        for(CubeCollision collision : collisions) {
            collision.drawAABB();
        }
    }
}
