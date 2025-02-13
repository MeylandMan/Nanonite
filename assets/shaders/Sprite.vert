#version 330 core
layout (location = 0) in vec2 in_Position;
layout (location = 1) in vec2 in_TexCoord;

out vec2 v_TexCoord;

uniform mat4 projection;

void main() {
    gl_Position = projection * vec4(in_Position, 0.0, 1.0);
    v_TexCoord = in_TexCoord;
}