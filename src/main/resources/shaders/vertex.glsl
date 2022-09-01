#type vertex
#version 330

layout(location = 0) in vec3 vertex;
layout(location = 1) in uint blockData;

uniform mat4 u_Proj;
uniform mat4 u_Camera;
uniform vec3 u_ChunkLocation;

void main() {
    float x = (u_ChunkLocation.x) + float(blockData & 0x1Fu) + vertex.x;
    float y = (u_ChunkLocation.y) + float((blockData >> 5) & 0x1Fu) + vertex.y;
    float z = (u_ChunkLocation.z) + float((blockData >> 10) & 0x1Fu) + vertex.z;

//    float x = (u_ChunkLocation.x) + 0 + vertex.x;
//    float y = (u_ChunkLocation.y) + 0 + vertex.y;
//    float z = (u_ChunkLocation.z) + 0 + vertex.z;

    vec4 finalV = vec4(x,y,z,1.0);

    gl_Position = u_Proj * u_Camera * finalV;
}