#type fragment
#version 330

in vec2 texCord;
in float diffuseFactor;
in float ambientFactor;

uniform sampler2D textureSamp;

out vec4 color;

void main(){
    vec3 tempColor = texture(textureSamp,texCord).xyz;
    color = vec4(ambientFactor*tempColor+diffuseFactor*tempColor,1);
}