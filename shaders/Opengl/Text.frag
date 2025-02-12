#version 330 core

in vec2 v_TexCoord;
out vec4 fragColor;

uniform sampler2D fontTexture;

void main() {

    vec4 sampled = texture(fontTexture, v_TexCoord);
    fragColor = sampled;
    //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}

