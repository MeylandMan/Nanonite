package net.Core.Rendering.UI;

import net.Core.Input;
import net.Core.Rendering.SpriteRenderer;
import net.Core.Rendering.Text.TextRenderer;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

public class UISlider extends UIElement {

    private String text;
    public Vector3f RectangleColor = new Vector3f();
    public Vector3f sliderColor = new Vector3f();
    float sliderX = 0.9f;
    float RectangleAlpha = 1.0f;

    public Vector3f Textcolor = new Vector3f();
    float Textalpha = 1.0f;

    boolean dragMove = false;
    public UISlider(String text, Vector3f position, Vector2f size, float value) {
        super(position, size);
        this.text = text;
    }

    public void setRectangleColor(Vector3f color) {
        RectangleColor = color;
    }
    public void setSliderColor(Vector3f color) {
        sliderColor = color;
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
        textRenderer.renderText(text, size.x/2, size.y/2, 0.5f, true);
        spriteRenderer.drawRectangle(new Vector3f(position), new Vector2f(size), RectangleColor, RectangleAlpha);
        spriteRenderer.drawRectangle(new Vector3f(sliderX, 0, 1), new Vector2f(0.1f, 1),
                sliderColor, RectangleAlpha);
    }

    private boolean intersects() {
        float x1 = position.x * (1+sliderX);
        float x2 = (position.x + (size.x*0.1f)) * (1+sliderX);
        return (Input.getMousePosition().x >= x1 && Input.getMousePosition().x <= x2
                && Input.getMousePosition().y >= position.y && Input.getMousePosition().y <= x2
        );
    }

    @Override
    public void update(float deltaTime) {
        if(Input.is_locked) {
            setSliderColor(new Vector3f(1, 0, 0));
            return;
        }

        if(!Input.isMouseButtonPressed(Input.MOUSE_LEFT))
            dragMove = false;

        if(intersects()) {
            setSliderColor(new Vector3f(0, 1, 0));
        } else {
            setSliderColor(new Vector3f(1, 0, 0));
        }
    }
}