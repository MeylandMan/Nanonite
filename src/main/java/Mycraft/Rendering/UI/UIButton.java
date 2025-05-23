package Mycraft.Rendering.UI;

import Mycraft.Core.Input;
import Mycraft.Rendering.SpriteRenderer;
import Mycraft.Rendering.Text.TextRenderer;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class UIButton extends UIElement {

    private String text;
    public Vector3f RectangleColor = new Vector3f();
    float RectangleAlpha = 1.0f;

    private Runnable onClick;

    public Vector3f Textcolor = new Vector3f();
    float Textalpha = 1.0f;

    public UIButton(String text, Vector3f position, Vector2f size) {
        super(position, size);
        this.text = text;

        this.onClick = () -> System.out.println("Default button action");
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
        textRenderer.renderText(text, size.x/2, size.y/2, 0.5f, TextRenderer.TextType.CENTER);
        spriteRenderer.drawRectangle(position, size, RectangleColor, RectangleAlpha);
    }

    private boolean intersects() {
        return (Input.getMousePosition().x >= position.x && Input.getMousePosition().x <= position.x + size.x
                && Input.getMousePosition().y >= position.y && Input.getMousePosition().y <= position.x + size.y
        );
    }

    public void setOnClickAction(Runnable action) {
        this.onClick = action;
    }

    @Override
    public void update(float deltaTime) {
        if(Input.is_locked) {
            setRectangleColor(new Vector3f(1, 0, 0));
            return;
        }

        if(intersects()) {
            setRectangleColor(new Vector3f(0, 1, 0));
            if(Input.isMouseButtonJustPressed(Input.MOUSE_LEFT)) {
                if(onClick != null)
                    onClick.run();
            }
        } else {
            setRectangleColor(new Vector3f(1, 0, 0));
        }
    }
}