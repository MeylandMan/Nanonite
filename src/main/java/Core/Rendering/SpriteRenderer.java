package Core.Rendering;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;

public class SpriteRenderer {
    Shader shader;
    Matrix4f matrix;

    public SpriteRenderer() {
        shader = new Shader();
        shader.CreateShader("Sprite.comp", "Sprite.frag");
    }

    public void getMatrixProjection(Matrix4f matrix) {
        this.matrix = matrix;
    }

    public void drawRectangle(int x, int y, int z, int width, int height, Vector3f color, float alpha) {

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.Bind();
        Matrix4f model = new Matrix4f().identity()
                .translate(x, y, 0)
                .scale(width, height, 1);

        shader.Uniform1i("textured", 0);
        shader.Uniform1f("Alpha", alpha);
        shader.Uniform3f("Color", color);
        shader.UniformMatrix4x4("model_projection", matrix.mul(model));
        glDrawArrays(GL_TRIANGLES, 0,  6);
        shader.UnBind();

        glDisable(GL_BLEND);
    }

    public void drawRectangle(Vector3f position, Vector2f size, Vector3f color, float alpha) {

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.Bind();
        Matrix4f model = new Matrix4f().identity()
                .translate(position)
                .scale(size.x, size.y, 1);

        shader.Uniform1i("textured", 0);
        shader.Uniform1f("Alpha", alpha);
        shader.Uniform3f("Color", color);
        shader.UniformMatrix4x4("model_projection", matrix.mul(model));
        glDrawArrays(GL_TRIANGLES, 0,  6);
        shader.UnBind();

        glDisable(GL_BLEND);
    }

    public void drawSprite(Texture texture, int x, int y, int width, int height) {

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.Bind();
        Matrix4f model = new Matrix4f().identity()
                .translate(x, y, 0)
                .scale(width, height, 1);

        texture.Bind();
        shader.Uniform1i("u_Texture", texture.getID());
        shader.Uniform1i("textured", 1);
        shader.Uniform1f("Alpha", 1);
        shader.Uniform3f("Color", 1, 1, 1);
        shader.UniformMatrix4x4("model_projection", matrix.mul(model));
        glDrawArrays(GL_TRIANGLES, 0,  6);
        shader.UnBind();

        glDisable(GL_BLEND);
    }

    public void drawSprite(Texture texture, int x, int y, int width, int height, Vector3f color) {

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.Bind();
        Matrix4f model = new Matrix4f().identity()
                .translate(x, y, 0)
                .scale(width, height, 1);

        texture.Bind();
        shader.Uniform1i("u_Texture", texture.getID());
        shader.Uniform1i("textured", 1);
        shader.Uniform1f("Alpha", 1);
        shader.Uniform3f("Color", color);
        shader.UniformMatrix4x4("model_projection", matrix.mul(model));
        glDrawArrays(GL_TRIANGLES, 0,  6);
        shader.UnBind();

        glDisable(GL_BLEND);
    }

    public void drawSprite(Texture texture, int x, int y, int width, int height, Vector3f color, float alpha) {
        glDisable(GL_CULL_FACE);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        shader.Bind();
        Matrix4f model = new Matrix4f().identity()
                .translate(x, y, 0)
                .scale(width, height, 1);

        texture.Bind();
        shader.Uniform1i("u_Texture", texture.getID());
        shader.Uniform1i("textured", 1);
        shader.Uniform1f("Alpha", alpha);
        shader.Uniform3f("Color", color);
        shader.UniformMatrix4x4("model_projection", matrix.mul(model));
        glDrawArrays(GL_TRIANGLES, 0,  6);
        shader.UnBind();

        glDisable(GL_BLEND);
    }
}
