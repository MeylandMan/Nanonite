package Mycraft.Models;

import java.util.Map;
import java.util.List;

public class Element {
    private List<Float> from; // Coordonnées de départ
    private List<Float> to;   // Coordonnées de fin
    private boolean opacity = true;
    private Map<String, Face> faces; // Liste des faces

    // Getters et Setters
    public List<Float> getFrom() {
        return from;
    }

    public float getFrom(int i) {
        return from.get(i);
    }

    public float getTo(int i) {
        return to.get(i);
    }

    public boolean isOpacity() {
        return opacity;
    }

    public void setOpacity(boolean opacity) {
        this.opacity = opacity;
    }

    public void setFrom(List<Float> from) {
        this.from = from;
    }

    public List<Float> getTo() {
        return to;
    }

    public void setTo(List<Float> to) {
        this.to = to;
    }

    public Map<String, Face> getFaces() {
        return faces;
    }

    public void setFaces(Map<String, Face> faces) {
        this.faces = faces;
    }
}

