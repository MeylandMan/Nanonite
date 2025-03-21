#version 330 core
out vec4 fragColor;


#define TEXTURE_LOADED 7
#define DEFAULT_GRASS_COLOR vec4(0.61, 0.80, 0.42, 1.0)

in vec2 v_TexCoords;
in vec3 fragPos;
in vec3 worldPos;
in float inside;
in float TextureIndex;

uniform vec3 cameraPos;
uniform vec3 fogColor;
uniform float renderDistance;

uniform sampler2D u_Textures[TEXTURE_LOADED];

void main() {
    if(inside == 0.0)
    discard;

    int index = int(TextureIndex);
    //fragColor = vec4(TextureIndex, TextureIndex, 0.0, 1.0);
    vec4 startingColor = texture(u_Textures[index], v_TexCoords);

    vec3 fogOrigin = vec3(0.0);
    float fogStart = renderDistance * 7.5;
    float fogEnd = fogStart * 1.25;
    float dist = length(worldPos - cameraPos);
    float frac = clamp((dist - fogStart) / (fogEnd - fogStart), 0.0, 1.0);

    vec4 specialColor = vec4(1.0);
    if(index == 2)
    specialColor = DEFAULT_GRASS_COLOR;

    vec4 result = mix(startingColor * specialColor, vec4(fogColor, 1.0), frac);

    fragColor = startingColor * specialColor;
    //fragColor = vec4(v_TexCoords, 1.0, 1.0);

}