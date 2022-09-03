#type fragment
#version 330

in vec3 fColor;
in float diffuse;

out vec4 color;

void main() {


    color = vec4((0.2*fColor) + diffuse * 2 * fColor, 1);
}