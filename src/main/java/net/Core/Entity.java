package net.Core;

import net.Core.Physics.CharacterBody;
import net.Core.Physics.Raycast;
import net.Core.Rendering.Shader;
import net.Core.Rendering.Texture;
import org.joml.*;

import java.util.ArrayList;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;

public class Entity {

    public Vector3d position;
    public Vector3d rotation;
    public Vector3d scale;

    public Vector3d velocity = new Vector3d();
    public Vector3d direction = new Vector3d(); // The direction the entity is looking at

    // Textures
    Texture alias;

    // Collisions
    public CharacterBody body;
    public Raycast raycast;

    String model; // Get the model from the cache directly

    public Entity() {
        this.position = new Vector3d();
        this.rotation = new Vector3d();
        this.scale = new Vector3d(1.0);

        this.body = new CharacterBody();
        this.raycast = new Raycast(position, direction);
    }

    public Entity(Vector3d position) {
        this.position = position;
        this.rotation = new Vector3d();
        this.scale = new Vector3d(1.0);

        this.body = new CharacterBody();
        this.raycast = new Raycast(position, direction);
    }

    public Entity(Vector3d position, Vector3d rotation) {
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector3d(1.0);

        this.body = new CharacterBody();
        this.raycast = new Raycast(position, direction);
    }

    public Entity(Vector3d position, Vector3d rotation, Vector3d scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;

        this.body = new CharacterBody();
        this.raycast = new Raycast(position, direction);
    }

    private void setTexture(String path) {
        alias = new Texture(path);
    }

    public void setModel(String modelName) {
        this.model = modelName;
        setTexture(getModel().texture);
    }
    public EntityModel getModel() { return Client.modelLoader.getEntityModel(model); }

    public void Draw(Shader shader) {
        EntityModel model = getModel();

        shader.Uniform1i("u_Texture", alias.getID());
        shader.UniformMatrix4x4("model", getModelMatrix());

        for(Element element : model.getElements()) {
            Vector3f from = new Vector3f(element.getFrom(0), element.getFrom(1), element.getFrom(2));
            Vector3f to = new Vector3f(element.getTo(0), element.getTo(1), element.getTo(2));

            shader.Uniform3f("From", from);
            shader.Uniform3f("To", to);

            glDrawArrays(GL_TRIANGLES, 0, 36);
        }
    }

    private Matrix4f getModelMatrix() {
        Vector3f center = new Vector3f(-0.5f, -1.0f, -0.5f);
        Vector3f pos = new Vector3f(position);


        return new Matrix4f().identity()
                .translate(pos)
                .rotateY((float)rotation.y)
                .rotateX((float)rotation.x)
                .rotateZ((float)rotation.z)
                .translate(center)
                .scale(new Vector3f(scale));
    }
}
