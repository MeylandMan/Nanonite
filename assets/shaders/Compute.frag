#version 330 core
in vec3 fragPos;
out vec4 fragColor;

void main() {
    vec3 color = fragPos * 0.5 + 0.5; // Coloration en fonction de la position
    fragColor = vec4(color, 1.0);
}