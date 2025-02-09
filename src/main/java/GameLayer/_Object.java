package GameLayer;

import GameLayer.Rendering.Model.CubeMesh;
import GameLayer.Rendering.Scene;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class _Object {
    public final CubeMesh mesh;

    private float positionX;
    private float positionY;
    private float positionZ;

    private float rotationX;
    private float rotationY;
    private float rotationZ;

    private float scaleX;
    private float scaleY;
    private float scaleZ;

    public _Object() {

        mesh = new CubeMesh();
        this.positionX = 0.f; this.positionY = 0.f; this.positionZ = 0.f;
        this.rotationX = 0.f; this.rotationY = 0.f; this.rotationZ = 0.f;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }
    public _Object(String texture) {

        mesh = new CubeMesh(texture);
        this.positionX = 0.f; this.positionY = 0.f; this.positionZ = 0.f;
        this.rotationX = 0.f; this.rotationY = 0.f; this.rotationZ = 0.f;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }
    public _Object(String texture, Vector3f position) {

        mesh = new CubeMesh(texture, position);
        this.positionX = position.x; this.positionY = position.y; this.positionZ = position.z;
        this.rotationX = 0.f; this.rotationY = 0.f; this.rotationZ = 0.f;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(new Vector3f(positionX, positionY, positionZ))                 // Translation
                .rotateXYZ(new Vector3f(rotationX, rotationY, rotationZ))                 // Rotation
                .scale(new Vector3f(scaleX, scaleY, scaleZ));                       // Scale
    }

    public _Object(String texture, Vector3f position, Vector3f rotation) {

        mesh = new CubeMesh(texture, position, rotation);
        this.positionX = position.x; this.positionY = position.y; this.positionZ = position.z;
        this.rotationX = rotation.x; this.rotationY = rotation.y; this.rotationZ = rotation.z;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }

    public _Object(String texture, Vector3f position, Vector3f rotation, Vector3f scale) {

        mesh = new CubeMesh(texture, position, rotation, scale);
        this.positionX = position.x; this.positionY = position.y; this.positionZ = position.z;
        this.rotationX = rotation.x; this.rotationY = rotation.y; this.rotationZ = rotation.z;
        this.scaleX = scale.x; this.scaleY = scale.y; this.scaleZ = scale.z;
    }

    public void setPosition(Vector3f position) {
        this.mesh.setPosition(position);
        this.positionX = position.x; this.positionY = position.y; this.positionZ = position.z;
    }

    public void setRotation(Vector3f rotation) {
        this.mesh.setRotation(rotation);
        this.rotationX = rotation.x; this.rotationY = rotation.y; this.rotationZ = rotation.z;
    }

    public void setScale(Vector3f scale) {
        this.mesh.setScale(scale);
        this.scaleX = scale.x; this.scaleY = scale.y; this.scaleZ = scale.z;
    }

    public Vector3f getPosition() {
        return new Vector3f(positionX, positionY, positionZ);
    }

    public Vector3f getRotation() {
        return new Vector3f(rotationX, rotationY, rotationZ);
    }

    public Vector3f getScale() {
        return new Vector3f(scaleX, scaleY, scaleZ);
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
