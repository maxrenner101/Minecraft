#type vertex
#version 330

layout(location = 0) in vec3 vertex;
layout(location = 1) in uint blockData;
layout(location = 2) in vec3 color;
layout(location = 3) in vec3 normal;

uniform mat4 u_Proj;
uniform mat4 u_Camera;
uniform vec3 u_ChunkLocation;

out vec3 fColor;
out float diffuse;

void main() {
    float x = (u_ChunkLocation.x) + float(blockData & 0x1Fu) + vertex.x;
    float y = (u_ChunkLocation.y) + float((blockData >> 5) & 0x1Fu) + vertex.y;
    float z = (u_ChunkLocation.z) + float((blockData >> 10) & 0x1Fu) + vertex.z;


    vec4 finalV = vec4(x,y,z,1.0);

    // light point at 10,10,2
    diffuse = dot(normalize(vec3(10,0,2) - vec3(x,y,z)), normalize(normal));

    fColor = color;

    gl_Position = u_Proj * u_Camera * finalV;
}