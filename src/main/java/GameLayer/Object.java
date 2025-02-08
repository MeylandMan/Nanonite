package GameLayer;

import GameLayer.Rendering.Model.CubeMesh;
import org.joml.Vector3f;

public class Object {
    private CubeMesh mesh;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public Object() {

        mesh = new CubeMesh();
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f();

    }
    public Object(String texture) {

        mesh = new CubeMesh(texture);
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f();
    }
    public Object(String texture, Vector3f position) {

        mesh = new CubeMesh(texture, position);
        this.position = new Vector3f(position);
        this.rotation = new Vector3f();
        this.scale = new Vector3f();
    }

    public Object(String texture, Vector3f position, Vector3f rotation) {

        mesh = new CubeMesh(texture, position, rotation);
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f();
    }

    public Object(String texture, Vector3f position, Vector3f rotation, Vector3f scale) {

        mesh = new CubeMesh(texture, position, rotation, scale);
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f(scale);
    }

    public void setPosition(Vector3f position) {
        this.mesh.position = position;
        this.position = position;
    }

    public void setRotation(Vector3f rotation) {
        this.mesh.rotation = rotation;
        this.rotation = rotation;
    }

    public void setScale(Vector3f scale) {
        this.mesh.scale = scale;
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }
}
