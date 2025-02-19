#version 330 core
out vec4 fragColor;


in vec2 v_TexCoords;
in vec3 fragPos;

uniform vec3 Color;
uniform float Alpha;
uniform bool textured;
uniform sampler2D u_Texture;

void main() {
    vec4 result = vec4(Color, Alpha);
    if(textured)
            result *= texture(u_Texture, v_TexCoords);
    fragColor = result;
}