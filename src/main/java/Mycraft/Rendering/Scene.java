package Mycraft.Rendering;

import GameLayer._Object;

import java.util.ArrayList;

public class Scene {
    private final ArrayList<_Object> objects = new ArrayList<>();

    public void AddObject(_Object object) {
        objects.add(object);
    }

    public void RemoveObject(_Object object) {
        objects.remove(object);
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
