package net.GameLayer;

import net.Core.Rendering.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public abstract class _Object {

    protected int length;
    protected VAO vao;
    protected VBO vbo;
    protected EBO ebo;

    protected int positionX;
    protected int positionY;
    protected int positionZ;

    private float rotationX;
    private float rotationY;
    private float rotationZ;

    private float scaleX;
    private float scaleY;
    private float scaleZ;

    public _Object() {
        this.positionX = 0; this.positionY = 0; this.positionZ = 0;
        this.rotationX = 0; this.rotationY = 0; this.rotationZ = 0;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }
    public _Object(String texture) {
        this.positionX = 0; this.positionY = 0; this.positionZ = 0;
        this.rotationX = 0; this.rotationY = 0; this.rotationZ = 0;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }
    public _Object(Vector3f position) {
        this.positionX = (int)position.x; this.positionY = (int)position.y; this.positionZ = (int)position.z;
        this.rotationX = 0; this.rotationY = 0; this.rotationZ = 0;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }

    public Matrix4f getModelMatrix() {
        return new Matrix4f().identity()
                .translate(new Vector3f(positionX, positionY, positionZ))                 // Translation
                .rotateXYZ(new Vector3f(rotationX, rotationY, rotationZ))                 // Rotation
                .scale(new Vector3f(scaleX, scaleY, scaleZ));                       // Scale
    }

    public _Object(Vector3f position, Vector3f rotation) {
        this.positionX = (int)position.x; this.positionY = (int)position.y; this.positionZ = (int)position.z;
        this.rotationX = rotation.x; this.rotationY = rotation.y; this.rotationZ = rotation.z;
        this.scaleX = 1.f; this.scaleY = 1.f; this.scaleZ = 1.f;
    }

    public _Object(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.positionX = (int)position.x; this.positionY = (int)position.y; this.positionZ = (int)position.z;
        this.rotationX = rotation.x; this.rotationY = rotation.y; this.rotationZ = rotation.z;
        this.scaleX = scale.x; this.scaleY = scale.y; this.scaleZ = scale.z;
    }

    public void setPosition(Vector3f position) {
        this.positionX = (int)position.x; this.positionY = (int)position.y; this.positionZ = (int)position.z;
    }

    public void setRotation(Vector3f rotation) {
        this.rotationX = rotation.x; this.rotationY = rotation.y; this.rotationZ = rotation.z;
    }

    public void setScale(Vector3f scale) {
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

    public void AddToScene(Scene scene) {
        scene.AddObject(this);
    }
    public static void AddObjectToScene(_Object obj, Scene scene) {
        scene.AddObject(obj);
    }

    public void DrawMesh(Shader shader) {
        vao.Bind();
        ebo.Bind();

        glDrawElements(GL_TRIANGLES, length, GL_UNSIGNED_INT, 0);

        vao.UnBind();
        ebo.UnBind();
    }

    public void Init(ArrayList<Byte> vertices, ArrayList<Integer> indices) {
        if (!GL.getCapabilities().OpenGL30) {
            throw new IllegalStateException("OpenGL 3.0 non disponible !");
        }

        vao = new VAO();
        vbo = new VBO();
        ebo = new EBO();

        VertexBufferLayout layout = new VertexBufferLayout();

        // Initialize them
        vbo.Init(vertices);
        layout.Add(3);
        layout.Add(2);
        layout.Add(3);
        layout.Add(1);
        vao.AddBuffer(vbo, layout);

        ebo.Init(indices);
    }
    
    public void Delete() {
        vao.Delete();
        vbo.Delete();
        ebo.Delete();
    }
}
