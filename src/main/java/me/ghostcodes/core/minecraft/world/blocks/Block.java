package me.ghostcodes.core.minecraft.world.blocks;

import lombok.Getter;
import me.ghostcodes.core.engine.rendering.Quad;
import me.ghostcodes.core.minecraft.world.Chunk;

public class Block {

    @Getter
    private final int localLocation;
    @Getter private final Quad[] quads = new Quad[6];
    @Getter private int cubeOffset;
    @Getter private final Chunk chunk;

    public Block(int localX, int localY, int localZ, Chunk chunk){
        this.chunk = chunk;
        localLocation = (0) | localX | localY << 5 | localZ << 10;

        int[] verts = new int[]{
                1,0,1, // 0
                0,0,1, // 1
                0,1,1,
                1,1,1,

                1,0,0, // 3
                0,0,0, // 2
                0,1,0,
                1,1,0
        };

        quads[0] = new Quad(0,0,0,1, verts[0],verts[1],verts[2],verts[3],verts[4],verts[5],verts[6],verts[7],verts[8],verts[9],verts[10],verts[11]);
        quads[1] = new Quad(4,0,0,-1,verts[12],verts[13],verts[14],verts[15],verts[16],verts[17],verts[18],verts[19],verts[20],verts[21],verts[22],verts[23]);
        quads[2] = new Quad(8,1,0,0,verts[3],verts[4],verts[5],verts[15],verts[16],verts[17],verts[18],verts[19],verts[20],verts[6],verts[7],verts[8]);
        quads[3] = new Quad(12,-1,0,0,verts[0],verts[1],verts[2],verts[12],verts[13],verts[14],verts[21],verts[22],verts[23],verts[9],verts[10],verts[11]);
        quads[4] = new Quad(16,0,1,0,verts[9],verts[10],verts[11],verts[6],verts[7],verts[8],verts[18],verts[19],verts[20],verts[21],verts[22],verts[23]);
        quads[5] = new Quad(20,0,-1,0,verts[0],verts[1],verts[2],verts[3],verts[4],verts[5],verts[15],verts[16],verts[17],verts[12],verts[13],verts[14]);

    }

    public void setCubeOffset(int offset){
        this.cubeOffset = offset;
        for(Quad q : quads){
            q.addToIndices(offset * 20 + offset+3*offset);
        }
    }

    public int getX() {
        return (localLocation & 0x1F) + chunk.getX();
    }
    public int getY() {
        return ((localLocation >> 5) & 0x1F) + chunk.getY();
    }
    public int getZ() {
        return ((localLocation >> 10) & 0x1F) + chunk.getZ();
    }
}
