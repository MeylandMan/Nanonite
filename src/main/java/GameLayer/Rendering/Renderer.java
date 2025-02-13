package GameLayer.Rendering;

import static org.lwjgl.opengl.GL20.*;

import GameLayer.Rendering.Model.CubeMesh;
import GameLayer.Rendering.Model.SpriteMesh;


public class Renderer {

    public void Draw(CubeMesh mesh) {
        mesh.Draw();
    }
    public void ClearColor() {
        glClearColor(0.53f, 0.81f, 0.92f, 0.f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    public void ClearRender() {

    }
    public void onResize() {

    }
    public void DrawScene(Scene scene, Shader shader) {
        scene.Draw(shader);
    }


}
