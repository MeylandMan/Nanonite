#version 330 core
out vec4 fragColor;

in vec2 v_TexCoords;
in vec3 fragPos;

uniform sampler2D u_Texture;

void main() {

    //vec3 color = fragPos * 0.5 + 0.5; // Coloration en fonction de la position
    fragColor = texture(u_Texture, v_TexCoords);
    //fragColor = vec4(color, 1.0);

}