package GameLayer.Rendering;

import GameLayer._Object;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class Scene {
    private final ArrayList<_Object> objects = new ArrayList<>();
    public void AddObject(_Object object) {
        objects.add(object);
    }
    public void Delete() {
        objects.clear();
    }

    public void Draw(Shader shader) {

        for (_Object obj: objects) {
            Matrix4f Model = new Matrix4f().identity()
                    .translate(obj.getPosition())                 // Translation
                    .rotateXYZ(obj.getRotation())                 // Rotation
                    .scale(obj.getScale());                       // Scale
            shader.Uniform1i("u_Texture", 0);
            shader.UniformMatrix4x4("u_Model", Model);
            obj.DrawMesh();
        }
    }

    public static void Draw(Scene scene, Renderer renderer, Shader shader) {
        renderer.DrawScene(scene, shader);
    }
}
