#version 330 core
layout(location = 0) in vec3 position;
out vec3 fragPos;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

void main() {

    vec3 vertices[36] = vec3[](
    // Face avant
    vec3(-1.0, -1.0,  1.0),
    vec3( 1.0, -1.0,  1.0),
    vec3( 1.0,  1.0,  1.0),
    vec3(-1.0, -1.0,  1.0),
    vec3( 1.0,  1.0,  1.0),
    vec3(-1.0,  1.0,  1.0),
    // Face arrière
    vec3(-1.0, -1.0, -1.0),
    vec3(-1.0,  1.0, -1.0),
    vec3( 1.0,  1.0, -1.0),
    vec3(-1.0, -1.0, -1.0),
    vec3( 1.0,  1.0, -1.0),
    vec3( 1.0, -1.0, -1.0),
    // Face gauche
    vec3(-1.0, -1.0, -1.0),
    vec3(-1.0, -1.0,  1.0),
    vec3(-1.0,  1.0,  1.0),
    vec3(-1.0, -1.0, -1.0),
    vec3(-1.0,  1.0,  1.0),
    vec3(-1.0,  1.0, -1.0),
    // Face droite
    vec3(1.0, -1.0, -1.0),
    vec3(1.0,  1.0, -1.0),
    vec3(1.0,  1.0,  1.0),
    vec3(1.0, -1.0, -1.0),
    vec3(1.0,  1.0,  1.0),
    vec3(1.0, -1.0,  1.0),
    // Face haute
    vec3(-1.0, 1.0, -1.0),
    vec3(-1.0, 1.0,  1.0),
    vec3(1.0,  1.0,  1.0),
    vec3(-1.0, 1.0, -1.0),
    vec3(1.0,  1.0,  1.0),
    vec3(1.0,  1.0, -1.0),
    // Face basse
    vec3(-1.0, -1.0, -1.0),
    vec3(1.0, -1.0, -1.0),
    vec3(1.0, -1.0,  1.0),
    vec3(-1.0, -1.0, -1.0),
    vec3(1.0, -1.0,  1.0),
    vec3(-1.0, -1.0,  1.0)
    );

    fragPos = vertices[gl_VertexID]; // Position du fragment
    gl_Position = projection * view * model * vec4(fragPos, 1.0);
}