package GameLayer.Rendering;

import static org.lwjgl.opengl.GL20.*;

import GameLayer.Rendering.Model.CubeMesh;
import GameLayer.Rendering.Model.SpriteMesh;


public class Renderer {

    public void Draw(SpriteMesh mesh) {
        mesh.Draw();
    }
    public void Draw(CubeMesh mesh) {
        mesh.Draw();
    }
    public void ClearColor() {
        glClearColor(0.05f, 0.01f, 0.1f, 0.f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    public void ClearRender() {

    }
    public void onResize() {

    }
}
