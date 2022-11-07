#type vertex
#version 460 core

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


layout(location = 0)in ivec2 data;

uniform float waterFog;
uniform vec3 chunkLocation;
uniform mat4 projection;
uniform mat4 view;
//uniform vec3 playerPos;

out float diffuseFactor;
out float ambientFactor;
out vec2 texCord;
out float waterFactor;
out float translucent;

void main(){

    if(data.y == 1){
        int blockType = (data.x >> 15) & 0xF;
        int side = (data.x >> 19) & 0x7;
        int vert = side*12 + 3 * (data.x >> 22);
        float x = chunkLocation.x + (data.x & 0x1F) + quads[vert];
        float y = chunkLocation.y + ((data.x >> 5) & 0x1F) + quads[vert + 1];
        float z = chunkLocation.z + ((data.x >> 10) & 0x1F) + quads[vert + 2];

        if(waterFog > 0)
        waterFactor = 1;
        else
        waterFactor = 0;

        vec3 normal = vec3((int(side) == 0) ? vec3(0, 1, 0) : (int(side) == 1) ? vec3(0, -1, 0) : (int(side) == 2) ? vec3(-1, 0, 0) : (int(side) == 3) ? vec3(1, 0, 0) : (int(side) == 4) ? vec3(0, 0, 1) : vec3(0, 0, -1));

        diffuseFactor = dot(normalize(normal), normalize(vec3(-2, 3, 0)));
        if (diffuseFactor < 0)
        diffuseFactor = 0;
        ambientFactor = 0.4;
        translucent = 0;

        if (blockType == 1){
            float texCords[] = float[](
            0.5,0.125,
            0.5,0,
            0.625,0,
            0.625,0.125
            );
            texCord = vec2(texCords[(data.x >> 22) * 2],texCords[((data.x >> 22) * 2)+1]);
        }
        else if (blockType == 2){
            float texCords[] = float[](
            0,0.125,
            0,0,
            0.125,0,
            0.125,0.125,
            0.125,0,
            0.125,0.125,
            0.25,0.125,
            0.25,0,
            0.25,0.125,
            0.25,0,
            0.375,0,
            0.375,0.125
            );
            texCord = vec2(texCords[(((side == 0) ? 8 : (side == 1) ? 16 : 0) + (data.x >> 22) * 2)],texCords[(((side == 0) ? 8 : (side == 1) ? 16 : 0) + (data.x >> 22) * 2)+1]);
        }
        else if (blockType == 3){
            float texCords[] = float[](
            0.375,0.125,
            0.375,0,
            0.5,0,
            0.5,0.125
            );
            texCord = vec2(texCords[(data.x >> 22) * 2],texCords[((data.x >> 22) * 2)+1]);
        }
        else if (blockType == 4){
            float texCords[] = float[](
            0.625,0.125,
            0.625,0,
            0.75,0,
            0.75,0.125
            );
            texCord = vec2(texCords[(data.x >> 22) * 2],texCords[((data.x >> 22) * 2)+1]);
            translucent = 1;
        }
        else if (blockType == 5){
            float texCords[] = float[](
            0.75,0.125,
            0.75,0,
            0.875,0,
            0.875,0.125
            );
            texCord = vec2(texCords[(data.x >> 22) * 2],texCords[((data.x >> 22) * 2)+1]);
        }

        gl_Position = projection * view * vec4(x, y, z, 1);
    }
}