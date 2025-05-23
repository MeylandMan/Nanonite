package Mycraft.Models;

import java.util.List;
import java.util.Map;

enum TextureType {
    SINGLE,
    MULTIPLE
}

public class Model {

    public String type = "SINGLE";
    String parent;

    List<Element> elements;

    String texture;
    Map<String, String> textures;

    boolean ambientOcclusion = true;

    public String getParent() {
        return parent;
    }
    public void setParent(String parent) {
        this.parent = parent;
    }


    // Set the model Type
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Ambient Occlusion
    public boolean getAO() { return ambientOcclusion; }
    public void setAO(boolean ao) { this.ambientOcclusion = ao; }

    // Single Texture
    public String getTexture() {
        return texture;
    }
    public void setTexture(String texture) { this.texture = texture; }

    // Multiple Textures
    public Map<String, String> getTextures() {
        return textures;
    }
    public void setTextures(Map<String, String> textures) {
        this.textures = textures;
    }
    public String getTexture(String key) {
        return this.textures.get(key);
    }

    public List<Element> getElements() {
        return elements;
    }
    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

}
