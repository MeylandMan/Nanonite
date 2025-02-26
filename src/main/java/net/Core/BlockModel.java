package net.Core;

import net.GameLayer.ChunkGen;

import java.util.List;
import java.util.Map;

public class BlockModel {
    private String parent;
    private Map<String, String> textures;
    private List<Element> elements;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Map<String, String> getTextures() {
        return textures;
    }

    public void setTextures(Map<String, String> textures) {
        this.textures = textures;
    }

    public String getTexture(String key) {
        return this.textures.get(key);
    }

    public String getTexture(int x) {
        String key = switch (x) {
            case 0 -> this.textures.get("north");
            case 1 -> this.textures.get("south");
            case 2 -> this.textures.get("east");
            case 3 -> this.textures.get("west");
            case 4 -> this.textures.get("up");
            case 5 -> this.textures.get("down");
            default -> "";
        };

        return this.textures.get(key);
    }

    public String[] TexturesToArray() {
        return this.textures.values().toArray(new String[0]);
    }
    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }
}
