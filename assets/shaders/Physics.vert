#version 330 core
layout(location = 0) in vec3 in_Position;


uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    mat4 MATRIX_VIEW_PROJECTION = projection * view * model;
    gl_Position = MATRIX_VIEW_PROJECTION * vec4(in_Position, 1.0);
}