package net.Core;

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
