#version 330 core
layout (location = 0) in vec3 in_Position;
layout (location = 1) in vec2 in_TexCoord;
layout (location = 2) in vec3 in_Color;

out vec2 v_TexCoord;
out vec3 v_Color;

uniform mat4 projection;

void main() {
    gl_Position = projection * vec4(in_Position, 1.0);
    v_TexCoord = in_TexCoord;
    v_Color = in_Color;
}