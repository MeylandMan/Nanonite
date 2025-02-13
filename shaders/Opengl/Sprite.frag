#version 330 core

out vec4 fragColor;

in vec2 v_TexCoord;

uniform vec3 Color;
uniform vec3 Alpha;

void main() {

    fragColor = vec4(Color, Alpha);
}