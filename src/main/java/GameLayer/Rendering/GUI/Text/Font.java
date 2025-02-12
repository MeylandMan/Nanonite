package GameLayer.Rendering.GUI.Text;

import java.util.HashMap;
import java.util.Map;

public class Font {
    public static class CharInfo {
        public int id;
        public float x, y, width, height;
        public float xOffset, yOffset, xAdvance;
    }

    private Map<Integer, CharInfo> characters = new HashMap<>();
    private int textureID;
    protected int textureWidth, textureHeight;

    public void addChar(CharInfo charInfo) {
        characters.put(charInfo.id, charInfo);
    }

    public CharInfo getChar(int id) {
        return characters.get(id);
    }

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public void setTextureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
    }
}
