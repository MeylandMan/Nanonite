package Core.Rendering;

import Core.Rendering.UI.UserInterface;
import GameLayer.World;
import org.joml.Vector3f;

import java.util.ArrayList;

import static Core.WorldEnvironment.*;
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
        Vector3f fogColor = interpolateFogColor(World.player.position.y);
        if(fogColor.x > SURFACE_DEFAULT_COLOR.x)
            glClearColor(SURFACE_DEFAULT_COLOR.x, SURFACE_DEFAULT_COLOR.y, SURFACE_DEFAULT_COLOR.z, 0.f);
        else
            glClearColor(fogColor.x, fogColor.y, fogColor.z, 0.f);
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
