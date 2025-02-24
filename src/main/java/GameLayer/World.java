package GameLayer;

import Core.Physics.CubeCollision;
import Core.Rendering.Camera;
import Core.Rendering.Shader;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class World {
    public static ArrayList<CubeCollision> worldCollisions;
    public Shader shader;
    public static final float GRAVITY = 0;

    public World() {
        worldCollisions = new ArrayList<>();
        shader = new Shader();
        shader.CreateShader("Physics.vert", "Physics.frag");
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

    public void onRender(Matrix4f view, Matrix4f projection) {
        glDisable(GL_BLEND);
        glDisable(GL_CULL_FACE);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);

        shader.Bind();
        shader.UniformMatrix4x4("view", view);
        shader.UniformMatrix4x4("projection", projection);

        for(CubeCollision collision : worldCollisions) {
            Matrix4f model = new Matrix4f().identity()
                            .translate(collision.position)
                            .scale(collision.size);
            shader.UniformMatrix4x4("model", model);
            shader.Uniform1f("borderThickness", 1);
            collision.drawAABB();
        }
    }
}
