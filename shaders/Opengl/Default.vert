#version 330 core
layout (location = 0) in vec3 in_Position;
layout (location = 1) in vec2 in_TexCoords;
//layout (location = 2) in vec3 in_Normal;

out vec2 v_TexCoord;

mat4 u_Model;
mat4 u_View;
mat4 u_Proj;

void main()
{
    v_TexCoord = in_TexCoords;
    mat4 MATRIX_VIEW_PROJECTION = u_Model * u_View * u_Proj;
    gl_Position = MATRIX_VIEW_PROJECTION * vec4(in_Position, 1.0);
}