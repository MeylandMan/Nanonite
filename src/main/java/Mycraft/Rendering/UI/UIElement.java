package Mycraft.Rendering.UI;

import Mycraft.Rendering.SpriteRenderer;
import Mycraft.Rendering.Text.TextRenderer;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class UIElement {

    protected Vector3f position;
    protected Vector2f size;

    public UIElement(Vector3f position, Vector2f size) {
        this.position = position;
        this.size = size;
    }

    public abstract void render(SpriteRenderer spriteRenderer, TextRenderer textRenderer);
    public abstract void update(float deltaTime);
}
