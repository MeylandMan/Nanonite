#version 330 core
layout (location = 0) in vec3 in_Position;
layout (location = 1) in vec2 in_TexCoord;
//layout (location = 2) in vec3 in_Normal;

out vec2 v_TexCoord;

uniform mat4 u_Model;
uniform mat4 u_View;
uniform mat4 u_Proj;

#define MODEL_VIEW_PROJECTION u_Proj * u_View * u_Model

void main()
{
    mat4 temp = mat4(1.0);
    v_TexCoord = in_TexCoord;
    gl_Position = MODEL_VIEW_PROJECTION * vec4(in_Position, 1.0);
}