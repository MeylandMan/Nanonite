#version 330 core
out vec4 fragColor;


//in vec2 v_TexCoords;
in vec3 fragPos;

//uniform vec3 Color;
//uniform float Alpha;
//uniform int textured;

//uniform sampler2D u_Texture;

void main() {

    /*
    if(textured = 0)
        fragColor = vec4(Color, Alpha);
    else
        fragColor = texture(u_Texture, v_TexCoords) * vec4(Color, Alpha);
    */
    vec3 color = fragPos * 0.5 + 0.5;
    fragColor = vec4(color, 1.0);
}