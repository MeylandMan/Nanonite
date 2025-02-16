#version 460 core

out vec3 fragPos;

out vec2 v_TexCoords;
out float TextureIndex;

uniform mat4 projection;
uniform mat4 view;
uniform vec3 Position;

const int CHUNK_SIZE_X = 16;
const int CHUNK_SIZE_Y = 255;
const int CHUNK_SIZE_Z = 16;

// Positions locales des sommets d’un cube

const vec2 texCoords[6] = vec2[](
vec2(0.0, 0.0), vec2(1.0, 0.0), vec2(1.0, 1.0),
vec2(0.0, 0.0), vec2(1.0, 1.0), vec2(0.0, 1.0)
);

uniform int BlockIDs[CHUNK_SIZE_X*CHUNK_SIZE_Y*CHUNK_SIZE_Z];
uniform bool BlockOpacity[CHUNK_SIZE_X*CHUNK_SIZE_Y*CHUNK_SIZE_Z];

struct Block {
    vec3 position;
    float id;
    //int opacity;
};

layout(std430, binding = 0) buffer BlockData {
    Block blocks[];
};

void main() {
    vec3 model[] = vec3[](
    // Face avant
    vec3(0.0, 0.0, 1.0), vec3(1.0, 0.0, 1.0), vec3(1.0, 1.0, 1.0),
    vec3(0.0, 0.0, 1.0), vec3(1.0, 1.0, 1.0), vec3(0.0, 1.0, 1.0),

    // Face arrière
    vec3(1.0, 0.0, 0.0), vec3(0.0, 0.0, 0.0), vec3(0.0, 1.0, 0.0),
    vec3(1.0, 0.0, 0.0), vec3(0.0, 1.0, 0.0), vec3(1.0, 1.0, 0.0),

    // Face gauche
    vec3(0.0, 0.0, 0.0), vec3(0.0, 0.0, 1.0), vec3(0.0, 1.0, 1.0),
    vec3(0.0, 0.0, 0.0), vec3(0.0, 1.0, 1.0), vec3(0.0, 1.0, 0.0),

    // Face droite
    vec3(1.0, 0.0, 1.0), vec3(1.0, 0.0, 0.0), vec3(1.0, 1.0, 0.0),
    vec3(1.0, 0.0, 1.0), vec3(1.0, 1.0, 0.0), vec3(1.0, 1.0, 1.0),

    // Face supérieure
    vec3(0.0, 1.0, 1.0), vec3(1.0, 1.0, 1.0), vec3(1.0, 1.0, 0.0),
    vec3(0.0, 1.0, 1.0), vec3(1.0, 1.0, 0.0), vec3(0.0, 1.0, 0.0),

    // Face inférieure
    vec3(0.0, 0.0, 0.0), vec3(1.0, 0.0, 0.0), vec3(1.0, 0.0, 1.0),
    vec3(0.0, 0.0, 0.0), vec3(1.0, 0.0, 1.0), vec3(0.0, 0.0, 1.0)
    );

    int cubeIndex = int(gl_VertexID/36);
    int vertexIndex = gl_VertexID % 36; // Assigner l'ID du sommet au cube

    fragPos = model[vertexIndex] + blocks[cubeIndex].position.xyz;

    // Assigner les coordonnées de texture (UV)
    v_TexCoords = texCoords[vertexIndex % 6];
    TextureIndex = blocks[cubeIndex].id;

    gl_Position = projection * view * vec4(fragPos+Position, 1.0);
}