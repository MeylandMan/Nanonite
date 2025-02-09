package GameLayer;

import GameLayer.Rendering.Model.CubeMesh;
import GameLayer.Rendering.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class _Object {
    public final CubeMesh mesh;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public _Object() {

        mesh = new CubeMesh();
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);

    }
    public _Object(String texture) {

        mesh = new CubeMesh(texture);
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);
    }
    public _Object(String texture, Vector3f position) {

        mesh = new CubeMesh(texture, position);
        this.position = new Vector3f(position);
        this.rotation = new Vector3f();
        this.scale = new Vector3f(1.0f);
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(position)                 // Translation
                .rotateXYZ(rotation)                 // Rotation
                .scale(scale);                       // Scale
    }

    public _Object(String texture, Vector3f position, Vector3f rotation) {

        mesh = new CubeMesh(texture, position, rotation);
        this.position = new Vector3f(position);
        this.rotation = new Vector3f(rotation);
        this.scale = new Vector3f(1.0f);
    }

    public _Object(String texture, Vector3f position, Vector3f rotation, Vector3f scale) {

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

    public void DrawMesh() {
        mesh.Draw();
    }

    public void AddToScene(Scene scene) {
        scene.AddObject(this);
    }
    public static void AddObjectToScene(_Object obj, Scene scene) {
        scene.AddObject(obj);
    }

    public void Delete() {
        mesh.Delete();
    }
}
