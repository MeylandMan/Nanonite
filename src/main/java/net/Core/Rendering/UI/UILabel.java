package net.Core.Rendering.UI;

import net.Core.Rendering.SpriteRenderer;
import net.Core.Rendering.Text.TextRenderer;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class UILabel extends UIElement {

    private String text;
    public Vector3f RectangleColor = new Vector3f();

    public Vector3f Textcolor = new Vector3f();
    float Textalpha = 1.0f;

    boolean centered = false;
    boolean bordered = false;
    public UILabel(String text, Vector3f position, float size, boolean centered, boolean border) {
        super(position, new Vector2f(size, 0));
        this.text = text;
        this.centered = centered;
        this.bordered = border;
    }

    public void setRectangleColor(Vector3f color) {
        RectangleColor = color;
    }
    public void setTextcolor(Vector3f color) {
        Textcolor = color;
    }
    public void setTextalpha(float alpha) {
        Textalpha = alpha;
    }

    @Override
    public void render(SpriteRenderer spriteRenderer, TextRenderer textRenderer) {

        float maxTextWidth = 0;
        float textHeight = textRenderer.getFont().lineHeight * size.x;
        int lineCount = 1;

        String[] lines = text.split("\n");
        for (String line : lines) {
            float lineWidth = textRenderer.getTextWidth(line, size.x);
            maxTextWidth = Math.max(maxTextWidth, lineWidth);
        }

        lineCount = lines.length;
        float totalHeight = lineCount * textHeight;

        textRenderer.renderText(text, position.x, position.y, size.x, centered);

        if (bordered) {
            spriteRenderer.drawRectangle(
                    position,
                    new Vector2f(maxTextWidth, totalHeight), // Largeur max et hauteur totale
                    RectangleColor,
                    0.5f
            );
        }

    }

    @Override
    public void update(float deltaTime) {

    }
}