package Renderer;

import static org.lwjgl.opengl.GL20.*;

import Renderer.Model.CubeMesh;
import Renderer.Model.SpriteMesh;


public class Renderer {

    public void Draw(SpriteMesh mesh) {
        mesh.Draw();
    }
    public void Draw(CubeMesh mesh) {
        mesh.Draw();
    }
    public void ClearColor() {
        glClearColor(0.f, 0.f, 0.f, 0.f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    public void ClearRender() {

    }
    public void onResize() {

    }
}
