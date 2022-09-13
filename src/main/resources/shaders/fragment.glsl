#type fragment
#version 330

in float diffuseFactor;

out vec4 color;

void main() {
    vec3 main = vec3(0,1,0.5);

    color = vec4(main * diffuseFactor * vec3(1,1,1) + main * 0.05,1);
}