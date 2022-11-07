#type fragment
#version 460 core

in vec2 texCord;
in float diffuseFactor;
in float ambientFactor;
in float waterFactor;
in float translucent;

uniform sampler2D textureSamp;

out vec4 color;

void main(){
    if(waterFactor > 0){
        vec4 tempColor = texture2D(textureSamp, texCord);
        float z = gl_FragCoord.z / gl_FragCoord.w;
        float fogFactor = (0 - z) / (0 - 10);
        fogFactor = clamp(fogFactor, 0.0, 1.0);
        color = vec4(diffuseFactor*vec3(1, 1, 1) + ambientFactor*vec3(1, 1, 1), 1);
        color *= tempColor;
        color = mix(color, vec4(0, 0, 0.5, 1), fogFactor);
        color*=color.aaaa;
        return;
    }

    if(translucent > 0){
        vec4 tempColor = texture2D(textureSamp, texCord);
        color=tempColor;
        color.w = 0.8;
        return;
    }

    vec4 tempColor = texture2D(textureSamp, texCord);
    color = vec4(diffuseFactor*vec3(1, 1, 1) + ambientFactor*vec3(1, 1, 1), 1);
    color *= tempColor;
}