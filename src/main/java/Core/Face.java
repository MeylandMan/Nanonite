package Core;

public class Face {
    private String texture;
    private byte cullFace;

    // Getters et Setters
    public String getTexture() {
        return texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public int getCullFace() {
        return cullFace;
    }

    public void setCullFace(byte cullFace) {
        this.cullFace = cullFace;
    }
}

