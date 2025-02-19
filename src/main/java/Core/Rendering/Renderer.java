package Core.Rendering;

import Core.Rendering.UI.UserInterface;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL20.*;


public class Renderer {

    ArrayList<UserInterface> interfaces = new ArrayList<>();

    public void addInterface(UserInterface user) {
        interfaces.add(user);
    }

    public void renderInterfaces() {
        for(UserInterface user : interfaces) {
            if(user.visible)
                user.render();
        }
    }

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
