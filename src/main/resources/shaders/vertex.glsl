#type vertex
#version 330

layout(location = 0)in vec3 pos;
layout(location = 1)in vec3 norm;
layout(location = 2)in vec3 localLocation;

uniform mat4 proj;
uniform vec3 chunkLocation;
uniform mat4 camera;

out float diffuseFactor;

void main() {

    float x = chunkLocation.x + pos.x + localLocation.x;
    float y = chunkLocation.y + pos.y + localLocation.y;
    float z = chunkLocation.z + pos.z + localLocation.z;

    vec3 light = vec3(0,50,-20);
    if(dot(normalize(norm), normalize(light-vec3(x,y,z))) < 0){
        diffuseFactor = 0;
    } else diffuseFactor = dot(normalize(norm), normalize(light-vec3(x,y,z)));

    gl_Position = proj * camera * vec4(x,y,z,1);
}