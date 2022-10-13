package me.ghostcodes.rendering;

import me.ghostcodes.minecraft.Minecraft;
import me.ghostcodes.minecraft.world.Chunk;
import me.ghostcodes.minecraft.world.World;
import me.ghostcodes.minecraft.world.blocks.Block;
import me.ghostcodes.rendering.shaders.Program;
import me.ghostcodes.utility.Convert;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.util.*;


import static org.lwjgl.opengl.GL46.*;
public class Renderer {

    private final int voxelProgram;
    private final List<VoxelBatch> voxelBatches = new ArrayList<>();
    private final Camera camera;

    private final Map<String, Integer> uniforms = new HashMap<>();
    private World world;

    public Renderer(String voxelFolder, Minecraft minecraft){
        this.world = minecraft.getWorld();
        File voxelFile = new File(voxelFolder);
        if(!voxelFile.isDirectory()) throw new IllegalStateException("Error finding voxel shader folder");
        if(Objects.requireNonNull(voxelFile.listFiles()).length == 0) throw new IllegalStateException("Error finding shaders in voxel shader folder.");
        voxelProgram = Program.createShaderProgram(voxelFile.listFiles());
        camera = Camera.get();
        createUniform("projection");
        createUniform("view");
        createUniform("chunkLocation");
    }

    public void render(double aspectRatio){
        glUseProgram(voxelProgram);
        setUniform("projection", camera.getProjection(aspectRatio));
        setUniform("view", camera.getView());

        // if loaded chunks was updated
        if(world.isUpdatedChunks()){
            // get new verts
            List<Block> blocks = new ArrayList<>();
            for(Chunk chunk : world.getLoadedChunks()){
                blocks.addAll(chunk.getBlocks());
            }
            for(Chunk chunk : world.getLoadedChunks()){
                if(!chunk.isLoaded()) {
                    for (int i = 0; i < Chunk.HEIGHT / VoxelBatch.HEIGHT; i++) {
                        List<Integer> finalVerts = new ArrayList<>();
                        VoxelBatch batch = new VoxelBatch(chunk.getX(), chunk.getY() + i, chunk.getZ());
                        batch.setRender(true);
                        for (int j = i * VoxelBatch.MAX_VOXELS; j < VoxelBatch.MAX_VOXELS + VoxelBatch.MAX_VOXELS * i; j++) {
                            Block block = chunk.getBlocks().get(j);
                            finalVerts.addAll(block.getData(blocks, batch.getY() * VoxelBatch.HEIGHT));
                        }
                        batch.setData(Convert.listToArr(finalVerts));
                        voxelBatches.add(batch);
                    }
                    chunk.setLoaded(true);
                }
            }

            // recalc which voxelbatches are rendered

            world.setUpdatedChunks(false);
        }
        // if camera was rotated/position updated
            // recalc which voxelbatches are rendered

        for(VoxelBatch batch : voxelBatches){
            if(batch.isRender()) {
                batch.bind();
                setUniform("chunkLocation", new Vector3f(batch.getX() * VoxelBatch.WIDTH, batch.getY() * VoxelBatch.HEIGHT, batch.getZ() * VoxelBatch.LENGTH));
                batch.draw();
                batch.unbind();
            }
        }
        glUseProgram(0);
    }

    private void createUniform(String key){
        int location = glGetUniformLocation(voxelProgram, key);
        if(location < 0) throw new IllegalStateException("Error getting uniform location: " + key);
        uniforms.put(key,location);
    }

    private void setUniform(String key, Matrix4f val){
        float[] fb = new float[16];
        val.get(fb);
        glUniformMatrix4fv(uniforms.get(key),false,fb);
    }

    private void setUniform(String key, Vector3f val){
        float[] fb = new float[]{val.x,val.y,val.z};
        glUniform3fv(uniforms.get(key),fb);
    }

    public void clean(){
        glDeleteProgram(voxelProgram);
    }

}
