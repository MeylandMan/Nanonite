package Mycraft.Models;

import java.util.ArrayList;
import java.util.List;

public class Face {

    private String texture;
    private byte cullFace = -1;
    private List<Byte> uv = new ArrayList<>();

    // Basic Constructor
    private Face() {
        List<Byte> DefaultUV = new ArrayList<>();
        DefaultUV.add((byte) 0);
        DefaultUV.add((byte) 0);
        DefaultUV.add((byte) 16);
        DefaultUV.add((byte) 16);

        uv = new ArrayList<>(DefaultUV);
    }
    
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

    // UV coordinates
    public List<Byte> getUV() { return uv; }
    public byte getUV(byte index) { return uv.get(index); }

    public void setUv(List<Byte> uv) { this.uv = uv; }
}

