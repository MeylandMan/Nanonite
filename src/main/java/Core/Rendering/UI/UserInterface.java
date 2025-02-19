package Core.Rendering.UI;

import Core.Rendering.SpriteRenderer;
import Core.Rendering.Text.TextRenderer;

import java.util.ArrayList;

public class UserInterface {
    private ArrayList<UIElement> elements;
    private final SpriteRenderer spriteRenderer;
    private final TextRenderer textRenderer;

    public boolean visible = true;

    public UserInterface(SpriteRenderer spriteRenderer, TextRenderer textRenderer) {
        this.spriteRenderer = spriteRenderer;
        this.textRenderer = textRenderer;
        this.elements = new ArrayList<>();
    }

    public void addElement(UIElement element) {
        elements.add(element);
    }

    public void removeElement(UIElement element) {
        elements.remove(element);
    }

    public void render() {
        for (UIElement element : elements) {
            element.render(spriteRenderer, textRenderer);
        }
    }

    public void update(float deltaTime) {
        for (UIElement element : elements) {
            element.update(deltaTime);
        }
    }

}
