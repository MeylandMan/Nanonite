#version 330 core
out vec4 fragColor;

in vec2 v_TexCoord;

uniform sampler2D u_Texture;

void main() {

    fragColor = texture(u_Texture, v_TexCoord);
    //fragColor = vec4(v_TexCoord, 0.0, 1.0); /*  (Showing the Texture mapping)   */
}