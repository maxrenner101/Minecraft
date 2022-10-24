package me.ghostcodes.rendering;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.ghostcodes.Main;
import me.ghostcodes.minecraft.Minecraft;
import me.ghostcodes.minecraft.world.Chunk;
import me.ghostcodes.minecraft.world.World;
import me.ghostcodes.minecraft.world.blocks.Block;
import me.ghostcodes.rendering.shaders.Program;
import me.ghostcodes.utility.Convert;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;


import static org.lwjgl.opengl.GL46.*;
public class Renderer {

    private final int voxelProgram;
    private final int renderDistance = 6;
    private final VoxelBatch[] voxelBatches = new VoxelBatch[renderDistance*renderDistance * (Chunk.HEIGHT / VoxelBatch.HEIGHT)];
    private final Camera camera;

    private final Map<String, Integer> uniforms = new HashMap<>();
    private final World world;
    private int atlasId;
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
        createUniform("textureSamp");

        try {
            PNGDecoder decoder = new PNGDecoder(
                    Main.class.getClassLoader().getResourceAsStream("textures/TextureAtlas.png"));
            ByteBuffer buf = ByteBuffer.allocateDirect(
                    4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();
            atlasId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, atlasId);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(),
                    decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void render(double aspectRatio){
        glUseProgram(voxelProgram);
        setUniform("projection", camera.getProjection(aspectRatio));
        setUniform("view", camera.getView());

        if(world.isUpdatedChunks()){
            cleanVoxelBatches();
            remapChunksToRender();
            world.setUpdatedChunks(false);
        }

        // if camera was rotated/position updated
            // recalc which voxelbatches are rendered

        for(VoxelBatch batch : voxelBatches){
            if(batch == null) continue;
            if(batch.isRender()) {
                batch.bind();
                setUniform("textureSamp", 0);
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, atlasId);
                setUniform("chunkLocation", new Vector3f(batch.getX() * VoxelBatch.WIDTH, batch.getY() * VoxelBatch.HEIGHT, batch.getZ() * VoxelBatch.LENGTH));
                batch.draw();
                glBindTexture(GL_TEXTURE_2D, 0);
                batch.unbind();
            }
        }
        glUseProgram(0);
    }


    private void cleanVoxelBatches(){
        List<VoxelBatch> voxelBatchesToRemove = new ArrayList<>();
        for(VoxelBatch batch : voxelBatches){
            if(batch == null) continue;
            boolean unload = true;
            for(int x = (int)(camera.getX()/Chunk.WIDTH) - renderDistance/2; x < (int)(camera.getX()/Chunk.WIDTH) + renderDistance/2; x++) {
                for (int z = (int) (-camera.getZ() / Chunk.LENGTH) - renderDistance / 2; z < (int) (-camera.getZ() / Chunk.LENGTH) + renderDistance / 2; z++) {
                    if (x == batch.getX() && z == batch.getZ()) {
                        unload = false;
                        break;
                    }
                }
            }
            if(unload)
                voxelBatchesToRemove.add(batch);
        }
        for (VoxelBatch voxelBatch : voxelBatchesToRemove) {
            for (int j = 0; j < voxelBatches.length; j++) {
                if(voxelBatches[j] == null) continue;
                if (voxelBatch.getX() == voxelBatches[j].getX() && voxelBatch.getY() == voxelBatches[j].getY() && voxelBatch.getZ() == voxelBatches[j].getZ()) {
                    for(Chunk chunk : world.getLoadedChunks()){
                        if(chunk == null) continue;
                        if(chunk.getX() == voxelBatches[j].getX() && chunk.getZ() == voxelBatches[j].getZ() && chunk.isLoaded())
                            chunk.setLoaded(false);
                    }
                    voxelBatches[j] = null;
                }
            }
        }
    }
    private void remapChunksToRender() {
        for(int x = (int)(camera.getX()/Chunk.WIDTH) - renderDistance/2; x < (int)(camera.getX()/Chunk.WIDTH) + renderDistance/2; x++) {
            for (int z = (int) (-camera.getZ() / Chunk.LENGTH) - renderDistance / 2; z < (int) (-camera.getZ() / Chunk.LENGTH) + renderDistance / 2; z++) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    if (chunk == null) continue;
                    if (chunk.getX() != x || chunk.getZ() != z)
                        continue;
                    if (!chunk.isLoaded()) {
                        Map<Integer, Block> blocks = new HashMap<>();
                        for (Block block : chunk.getBlocks())
                            blocks.put(block.getLocalX() | block.getLocalY() << 6 | block.getLocalZ() << 15, block);
                        for (int i = 0; i < Chunk.HEIGHT / VoxelBatch.HEIGHT; i++) {
                            List<Integer> finalVerts = new ArrayList<>();
                            VoxelBatch batch = new VoxelBatch(chunk.getX(), chunk.getY() + i, chunk.getZ());
                            batch.setRender(true);
                            for (int j = i * VoxelBatch.MAX_VOXELS; j < VoxelBatch.MAX_VOXELS + VoxelBatch.MAX_VOXELS * i; j++) {
                                Block block = chunk.getBlocks()[j];
                                List<Integer> data = block.getData(blocks, batch.getY() * VoxelBatch.HEIGHT);
                                finalVerts.addAll(data);
                            }
                            batch.setData(Convert.listToArr(finalVerts));
                            for(int k = 0; k < voxelBatches.length; k++){
                                if(voxelBatches[k] == null){
                                    voxelBatches[k] = batch;
                                    break;
                                }
                            }
                        }
                        chunk.setLoaded(true);
                    }
                }
            }
        }
    }

    private void createUniform(String key){
        int location = glGetUniformLocation(voxelProgram, key);
        if(location < 0) throw new IllegalStateException("Error getting uniform location: " + key);
        uniforms.put(key,location);
    }

    private void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
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
