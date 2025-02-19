package Core.Rendering.UI;

import Core.Rendering.SpriteRenderer;
import Core.Rendering.Text.TextRenderer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class UIButton extends UIElement {

    public Vector3f RectangleColor = new Vector3f();
    float RectangleAlpha = 1.0f;

    public Vector3f Textcolor = new Vector3f();
    float Textalpha = 1.0f;

    public UIButton(Vector3f position, Vector2f size) {
        super(position, size);
    }

    public void setRectangleColor(Vector3f color) {
        RectangleColor = color;
    }
    public void setRectangleAlpha(float alpha) {
        RectangleAlpha = alpha;
    }
    public void setTextcolor(Vector3f color) {
        Textcolor = color;
    }
    public void setTextalpha(float alpha) {
        Textalpha = alpha;
    }

    @Override
    public void render(SpriteRenderer spriteRenderer, TextRenderer textRenderer) {
        spriteRenderer.drawRectangle(position, size, RectangleColor, RectangleAlpha);
    }

    @Override
    public void update(float deltaTime) {

    }
}