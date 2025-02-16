#version 330 core
out vec4 fragColor;

#define TEXTURE_LOADED 3

in vec2 v_TexCoords;
in vec3 fragPos;
in float TextureIndex;

uniform sampler2D u_Textures[TEXTURE_LOADED];

void main() {
    int index = int(TextureIndex);
    //fragColor = vec4(TextureIndex, TextureIndex, 0.0, 1.0);
    fragColor = texture(u_Textures[index], v_TexCoords);
    //fragColor = vec4(color, 1.0);

}