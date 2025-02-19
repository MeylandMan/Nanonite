package Core.Rendering.UI;

import Core.Input;
import Core.Rendering.SpriteRenderer;
import Core.Rendering.Text.TextRenderer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

public class UIButton extends UIElement {

    private String text;
    public Vector3f RectangleColor = new Vector3f();
    float RectangleAlpha = 1.0f;

    public Vector3f Textcolor = new Vector3f();
    float Textalpha = 1.0f;

    public UIButton(String text, Vector3f position, Vector2f size) {
        super(position, size);
        this.text = text;
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
        textRenderer.renderText(text, size.x/2, size.y/2, 0.01f, true);
        spriteRenderer.drawRectangle(position, size, RectangleColor, RectangleAlpha);
    }

    private boolean intersects() {
        return (Input.getMousePosition().x >= position.x && Input.getMousePosition().x <= position.x + size.x
                && Input.getMousePosition().y >= position.y && Input.getMousePosition().y <= position.x + size.y
        );
    }
    @Override
    public void update(float deltaTime) {
        if(intersects()) {
            setRectangleColor(new Vector3f(0, 1, 0));
            if(Input.isMouseButtonJustPressed(Input.MOUSE_LEFT)) {
                System.out.println("You clicked the Button");
            }
        } else {
            setRectangleColor(new Vector3f(1, 0, 0));
        }
    }
}