#version 330 core

in vec3 v_Color;
in vec2 v_TexCoord;
out vec4 fragColor;

uniform sampler2D fontTexture;

void main() {

    vec4 sampled = texture(fontTexture, v_TexCoord);

    if(sampled.r == 0)
        discard;
    fragColor = vec4(v_Color, sampled.r);
    //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}

