#version 330 core
out vec4 fragColor;

in vec2 v_TexCoord;
in float v_TexIndex;

#define TEXTURE_LOADED 3
#define DEFAULT_GRASS_COLOR vec4(0.61, 0.80, 0.42, 1.0)

uniform sampler2D u_Textures[TEXTURE_LOADED];

void main() {

    int index = int(v_TexIndex);
    if(index == 2)
        fragColor = texture(u_Textures[index], v_TexCoord) * DEFAULT_GRASS_COLOR;
    else
        fragColor = texture(u_Textures[index], v_TexCoord);
    //fragColor = vec4(v_TexIndex, v_TexIndex, v_TexIndex, 1.0); /*  (Showing the Texture indices)   */
    //fragColor = vec4(v_TexCoord, 0.0, 1.0); /*  (Showing the Texture mapping)   */
}