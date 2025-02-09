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
        for(_Object obj : objects) {
            obj.Delete();
        }
        objects.clear();
    }

    public void Draw(Shader shader) {

        for (_Object obj: objects) {
            shader.Uniform1i("u_Texture", 0);
            shader.UniformMatrix4x4("u_Model", obj.getModelMatrix());
            obj.DrawMesh();
        }
    }

    public static void Draw(Scene scene, Renderer renderer, Shader shader) {
        renderer.DrawScene(scene, shader);
    }
}
