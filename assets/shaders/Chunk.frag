#version 330 core
out vec4 fragColor;


#define TEXTURE_LOADED 9
#define DEFAULT_GRASS_COLOR vec4(0.61, 0.80, 0.42, 1.0)
#define DEFAULT_WATER_COLOR vec4(0.14, 0.21, 0.42, 1.0)

in vec2 v_TexCoords;
in vec3 fragPos;
in vec3 worldPos;
in vec3 v_Normal;
in float TextureIndex;

uniform vec3 cameraPos;
uniform vec3 fogColor;
uniform float renderDistance;
uniform float fogDistance;
uniform bool UnderWater;

uniform sampler2D u_Textures[TEXTURE_LOADED];

void main() {

    int index = int(TextureIndex);
    //fragColor = vec4(TextureIndex, TextureIndex, 0.0, 1.0);
    vec4 startingColor = texture(u_Textures[index], v_TexCoords);

    if(UnderWater)
        startingColor *= DEFAULT_WATER_COLOR;

    vec3 fogOrigin = vec3(0.0);
    float fogStart = renderDistance * fogDistance;
    float fogEnd = fogStart * 1.25;
    float dist = length(worldPos - cameraPos);
    float frac = clamp((dist - fogStart) / (fogEnd - fogStart), 0.0, 1.0);

    vec4 specialColor = vec4(1.0);
    if(index == 2)
        specialColor = DEFAULT_GRASS_COLOR;

    vec4 fogResult = mix(startingColor * specialColor, vec4(fogColor, 1.0), frac);

    // Directional Lighting
    vec3 lightColor = vec3(1.0);
    vec3 diffuseColor = vec3(0.5);

    vec3 lightDir = vec3(0.8, 0.8, 0.6);

    // Ambient
    float ambientStrength = 0.5;
    vec3 ambient = ambientStrength * lightColor;

    // Diffuse
    float NDotL = max(dot(v_Normal, lightDir), 0.0);
    vec3 diffuse = NDotL * diffuseColor;

    vec4 lightResult = mix(vec4(ambient + diffuse, 1.0), vec4(fogColor, 1.0), frac);

    vec4 result = fogResult * lightResult;

    fragColor = result;
    //fragColor = vec4(max(v_Normal, vec3(0.0)), 1.0);
    //fragColor = vec4(v_TexCoords, 1.0, 1.0);

}