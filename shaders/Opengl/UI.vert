#version 330 core
layout (location = 0) in vec3 in_Position;
layout (location = 1) in vec3 in_Color;


out vec3 v_Color;

uniform float u_Ratio;

void main()
{
    v_Color = in_Color;
    gl_Position = vec4(vec3(in_Position.x/u_Ratio, in_Position.y, 0.0), 1.0);
}