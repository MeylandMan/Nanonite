#version 330 core
out vec4 fragColor;

#define TEXTURE_LOADED 3
#define DEFAULT_GRASS_COLOR vec4(0.61, 0.80, 0.42, 1.0)

in vec2 v_TexCoords;
in vec3 fragPos;
in float TextureIndex;

uniform sampler2D u_Textures[TEXTURE_LOADED];

void main() {
    int index = int(TextureIndex);
    //fragColor = vec4(TextureIndex, TextureIndex, 0.0, 1.0);

    if(index == 2)
        fragColor = texture(u_Textures[index], v_TexCoords) * DEFAULT_GRASS_COLOR;
    else
        fragColor = texture(u_Textures[index], v_TexCoords);
    //fragColor = vec4(v_TexCoords, 1.0, 1.0);

}