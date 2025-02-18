package GameLayer.Rendering;

import GameLayer.Chunk;
import GameLayer.Rendering.Model.SpriteMesh;
import GameLayer._Object;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Scene {
    private final ArrayList<_Object> objects = new ArrayList<>();
    SpriteMesh surface2D;

    public void create2DSurface(SpriteMesh sm) {
        surface2D = sm;
    }

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
            obj.DrawMesh(shader);

        }



    }



    public static void Draw(Scene scene, Renderer renderer, Shader shader) {
        renderer.DrawScene(scene, shader);
    }
}
