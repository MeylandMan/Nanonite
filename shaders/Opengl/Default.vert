#version 330 core
layout (location = 0) in vec3 in_Position;
layout (location = 1) in vec3 in_Color;

out vec3 v_Color;

void main()
{
    v_Color = in_Color;
    gl_Position = vec4(in_Position, 1.0);
}