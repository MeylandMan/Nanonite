package Mycraft.Models;

import java.util.ArrayList;
import java.util.List;

public class Face {

    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int UP = 4;
    public static final int DOWN = 5;

    public static final int UNFACE = -1;
    public static final int UNCULL = -2;

    private String texture;
    private byte cullFace = -1;
    private List<Byte> uv;

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

