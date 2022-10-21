#type vertex
#version 330

layout(location = 0)in ivec2 data;

uniform vec3 chunkLocation;
uniform mat4 projection;
uniform mat4 view;

const float quads[] = float[](
0,1,0,
0,1,1,
1,1,1,
1,1,0, // top

0,0,0,
0,0,1,
1,0,1,
1,0,0, // bottom

0,0,0,
0,1,0,
0,1,1,
0,0,1, // left

1,0,0,
1,1,0,
1,1,1,
1,0,1, // right

0,0,1,
0,1,1,
1,1,1,
1,0,1,// front

0,0,0,
0,1,0,
1,1,0,
1,0,0// back
);

out vec4 fColor;

void main(){

    if(data.y == 1){
        int blockType = (data.x >> 15) & 0xF;
        int side = (data.x >> 19) & 0x7;
        int vert = side*12 + 3 * (data.x >> 22);
        float x = chunkLocation.x + (data.x & 0x1F) + quads[vert];
        float y = chunkLocation.y + ((data.x >> 5) & 0x1F) + quads[vert + 1];
        float z = chunkLocation.z + ((data.x >> 10) & 0x1F) + quads[vert + 2];

        vec3 normal = vec3((int(side) == 0) ? vec3(0, 1, 0) : (int(side) == 1) ? vec3(0, -1, 0) : (int(side) == 2) ? vec3(-1, 0, 0) : (int(side) == 3) ? vec3(1, 0, 0) : (int(side) == 4) ? vec3(0, 0, 1) : vec3(0, 0, -1));

        float diffuseFactor = dot(normalize(normal), normalize(vec3(-2, 10, -4)));
        if (diffuseFactor < 0)
        diffuseFactor = 0;
        float ambientFactor = 0.2;

        vec3 tempColor;
        if (blockType == 1)
        tempColor = vec3(0.3, 0.3, 0.3);
        else if (blockType == 2)
        tempColor = vec3(0, 1, 0);
        else if (blockType == 3)
        tempColor = vec3(0.36, 0.2, 0.03);

        fColor = vec4(diffuseFactor*tempColor + ambientFactor*tempColor, 1);

        gl_Position = projection * view * vec4(x, y, z, 1);
    }
}