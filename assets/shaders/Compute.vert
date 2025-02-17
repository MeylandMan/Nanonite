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

struct BlockFace {
    vec3 position;
    float padding;

    float id;
    float FaceID;
};

layout(std430, binding = 0) buffer BlockData {
    BlockFace faces[];
};

void main() {

    int faceIndex = int(gl_VertexID/6);

    vec3 FaceVertices[6];
    switch(int(faces[faceIndex].FaceID)) {
        // FRONT
        case 0:
            FaceVertices = vec3[](
            vec3(0.0, 0.0, 1.0), vec3(1.0, 0.0, 1.0), vec3(1.0, 1.0, 1.0),
            vec3(0.0, 0.0, 1.0), vec3(1.0, 1.0, 1.0), vec3(0.0, 1.0, 1.0)
            );
            break;
        // BACK
        case 1:
            FaceVertices = vec3[](
            vec3(1.0, 0.0, 0.0), vec3(0.0, 0.0, 0.0), vec3(0.0, 1.0, 0.0),
            vec3(1.0, 0.0, 0.0), vec3(0.0, 1.0, 0.0), vec3(1.0, 1.0, 0.0)
            );
            break;
        // RIGHT
        case 2:
            FaceVertices = vec3[](
            vec3(0.0, 0.0, 0.0), vec3(0.0, 0.0, 1.0), vec3(0.0, 1.0, 1.0),
            vec3(0.0, 0.0, 0.0), vec3(0.0, 1.0, 1.0), vec3(0.0, 1.0, 0.0)
            );
            break;
        // LEFT
        case 3:
            FaceVertices = vec3[](
            vec3(1.0, 0.0, 1.0), vec3(1.0, 0.0, 0.0), vec3(1.0, 1.0, 0.0),
            vec3(1.0, 0.0, 1.0), vec3(1.0, 1.0, 0.0), vec3(1.0, 1.0, 1.0)
            );
            break;
        // TOP
        case 4:
            FaceVertices = vec3[](
            vec3(0.0, 1.0, 1.0), vec3(1.0, 1.0, 1.0), vec3(1.0, 1.0, 0.0),
            vec3(0.0, 1.0, 1.0), vec3(1.0, 1.0, 0.0), vec3(0.0, 1.0, 0.0)
            );
            break;
        // BOTTOM
        case 5:
            FaceVertices = vec3[](
            vec3(0.0, 0.0, 0.0), vec3(1.0, 0.0, 0.0), vec3(1.0, 0.0, 1.0),
            vec3(0.0, 0.0, 0.0), vec3(1.0, 0.0, 1.0), vec3(0.0, 0.0, 1.0)
            );
            break;
    }
    int vertexIndex = gl_VertexID % 6;

    fragPos = FaceVertices[vertexIndex] + faces[faceIndex].position.xyz;

    // Assigner les coordonnées de texture (UV)
    v_TexCoords = texCoords[vertexIndex % 6];
    TextureIndex = faces[faceIndex].id;

    gl_Position = projection * view * vec4(fragPos+Position, 1.0);
}

