package me.ghostcodes.rendering;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.ghostcodes.Main;
import me.ghostcodes.minecraft.Minecraft;
import me.ghostcodes.minecraft.world.Chunk;
import me.ghostcodes.minecraft.world.World;
import me.ghostcodes.minecraft.world.blocks.Block;
import me.ghostcodes.minecraft.world.blocks.BlockType;
import me.ghostcodes.rendering.shaders.Program;
import me.ghostcodes.utility.Convert;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL46.*;
public class Renderer {

    private final int voxelProgram;
    private final int renderDistance = 8;
    private final VoxelBatch[] voxelBatches = new VoxelBatch[renderDistance*renderDistance * (Chunk.HEIGHT / VoxelBatch.HEIGHT)];
    private final Camera camera;
    private final Map<String, Integer> uniforms = new HashMap<>();
    private World world;
    private final Minecraft minecraft;
    private int atlasId;
    public Renderer(String voxelFolder, Minecraft minecraft){
        this.minecraft = minecraft;
        File voxelFile = new File(Main.class.getClassLoader().getResource(voxelFolder).getPath());
        if(!voxelFile.isDirectory()) throw new IllegalStateException("Error finding voxel shader folder");
        if(Objects.requireNonNull(voxelFile.listFiles()).length == 0) throw new IllegalStateException("Error finding shaders in voxel shader folder.");
        voxelProgram = Program.createShaderProgram(voxelFile.listFiles());
        camera = Camera.get();
        createUniform("projection");
        createUniform("view");
        createUniform("chunkLocation");
        createUniform("textureSamp");
        createUniform("waterFog");

        try {
            PNGDecoder decoder = new PNGDecoder(
                    new FileInputStream(Main.class.getClassLoader().getResource("textures/TextureAtlas.png").getPath()));
            ByteBuffer buf = ByteBuffer.allocateDirect(
                    4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();
            atlasId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, atlasId);
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(),
                    decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
            glGenerateMipmap(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, 0);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void render(double aspectRatio){
        switch (minecraft.state) {
            case TITLE -> glClearColor(0, 0, 0, 1);
            case RUNNING -> {
                if (this.world == null) {
                    this.world = minecraft.getWorld();
                }
                glClearColor(0.5f, 1, 1, 1);
                glUseProgram(voxelProgram);
                setUniform("projection", camera.getProjection(aspectRatio));
                setUniform("view", camera.getView());
                if (loading) {
                    remapChunksToRender();
                }
                if (world.isUpdatedChunks()) {
                    remapChunksToRender();

                    world.setUpdatedChunks(false);
                }
                if(camera.getX() != camera.getLastX() || camera.getY() != camera.getLastY() || camera.getZ() != camera.getLastZ()) {
                    Block block = world.getBlock((int) camera.getX(), (int) camera.getY(), (int) -camera.getZ());
                    if (block != null) {
                        if (block.getType() == BlockType.WATER) {
                            setUniform("waterFog", 1f);
                        } else {
                            setUniform("waterFog", 0f);
                        }
                    }
                }
                for (VoxelBatch batch : voxelBatches) {
                    if (batch == null) continue;
                    if (batch.isRender()) {
                        setUniform("textureSamp", 0);
                        glActiveTexture(GL_TEXTURE0);
                        glBindTexture(GL_TEXTURE_2D, atlasId);
                        setUniform("chunkLocation", new Vector3f(batch.getX() * VoxelBatch.WIDTH, batch.getY() * VoxelBatch.HEIGHT, batch.getZ() * VoxelBatch.LENGTH));
                        batch.draw();
                        glBindTexture(GL_TEXTURE_2D, 0);
                        batch.unbind();
                    }
                }
                for (VoxelBatch batch : voxelBatches) {
                    if (batch == null) continue;
                    if (batch.isRender()) {
                        setUniform("textureSamp", 0);
                        glActiveTexture(GL_TEXTURE0);
                        glBindTexture(GL_TEXTURE_2D, atlasId);
                        setUniform("chunkLocation", new Vector3f(batch.getX() * VoxelBatch.WIDTH, batch.getY() * VoxelBatch.HEIGHT, batch.getZ() * VoxelBatch.LENGTH));
                        batch.tDraw();
                        glBindTexture(GL_TEXTURE_2D, 0);
                        batch.unbind();
                    }
                }
                glUseProgram(0);
            }
        }
    }

    private final List<BatchLoader> loaders = new ArrayList<>();

    private class BatchLoader extends Thread {


        private final Map<int[], int[][]> finalDatas = new HashMap<>();

        private final Chunk[] chunks;
        private final float x,z;

        public BatchLoader(float x, float z, Chunk[] chunks){
            this.chunks = chunks;
            this.x = x;
            this.z = z;
        }

        @Override
        public void run(){
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
                if(unload) {
                    voxelBatchesToRemove.add(batch);
                }
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

            Map<String, Block> blocks = new HashMap<>();
            for(int x = (int)(this.x/Chunk.WIDTH) - renderDistance/2; x < (int)(this.x/Chunk.WIDTH) + renderDistance/2; x++) {
                for (int z = (int) (-this.z / Chunk.LENGTH) - renderDistance / 2; z < (int) (-this.z / Chunk.LENGTH) + renderDistance / 2; z++) {
                    for (Chunk chunk : chunks) {
                        if (chunk == null) continue;
                        if (chunk.getX() != x || chunk.getZ() != z)
                            continue;
                        for (Block block : chunk.getBlocks())
                            blocks.put(block.getX() + " " + block.getY() + " " + block.getZ(), block);
                    }
                }
            }

            for(int x = (int)(this.x/Chunk.WIDTH) - renderDistance/2; x < (int)(this.x/Chunk.WIDTH) + renderDistance/2; x++) {
                for (int z = (int) (-this.z / Chunk.LENGTH) - renderDistance / 2; z < (int) (-this.z / Chunk.LENGTH) + renderDistance / 2; z++) {
                    for (Chunk chunk : chunks) {
                        if (chunk == null) continue;
                        if (chunk.getX() != x || chunk.getZ() != z)
                            continue;
                        for (int i = 0; i < Chunk.HEIGHT / VoxelBatch.HEIGHT; i++) {
                            List<Integer> finalVerts = new ArrayList<>();
                            List<Integer> tFinalVerts = new ArrayList<>();
                            int[] batch = new int[]{chunk.getX(), chunk.getY() + i, chunk.getZ()};
                            for (int j = i * VoxelBatch.MAX_VOXELS; j < VoxelBatch.MAX_VOXELS + VoxelBatch.MAX_VOXELS * i; j++) {
                                Block block = chunk.getBlocks()[j];
                                if(block.getType() == BlockType.WATER)
                                    tFinalVerts.addAll(block.getData(blocks, batch[1] * VoxelBatch.HEIGHT));
                                else finalVerts.addAll(block.getData(blocks, batch[1] * VoxelBatch.HEIGHT));
                            }
                            finalDatas.put(batch, new int[][]{Convert.listToArr(finalVerts), Convert.listToArr(tFinalVerts)});
                        }
                        chunk.setLoaded(true);
                    }
                }
            }
        }
    }

    private boolean loading = false;

    private void remapChunksToRender() {
        if(!loading) {
            BatchLoader loader = new BatchLoader(camera.getX(), camera.getZ(), world.getLoadedChunks());
            loader.start();
            loaders.add(loader);
            loading = true;
        }
        if(loading) {
            List<BatchLoader> loadersToRemove = new ArrayList<>();
            for(BatchLoader loader : loaders) {
                if (!loader.isAlive()) {
                    loader.finalDatas.forEach((k, v) -> {
                        boolean reload = false;
                        for(VoxelBatch batch : voxelBatches){
                            if(batch == null) continue;
                            if (batch.getX() == k[0] && batch.getY() == k[1] && batch.getZ() == k[2]) {
                                reload = true;
                                break;
                            }
                        }
                        if(!reload) {
                            VoxelBatch batch = new VoxelBatch(k[0], k[1], k[2]);
                            batch.setData(v[0], v[1]);
                            for (int d = 0; d < voxelBatches.length; d++) {
                                if (voxelBatches[d] == null) {
                                    voxelBatches[d] = batch;
                                    break;
                                }
                            }
                            batch.setRender(true);
                        } else {
                            for(VoxelBatch batch : voxelBatches){
                                if(batch == null) continue;
                                if(batch.getX() == k[0] && batch.getY() == k[1] && batch.getZ() == k[2]){
                                    batch.setData(v[0], v[1]);
                                    break;
                                }
                            }
                        }
                    });

                    loadersToRemove.add(loader);
                }
            }
            loaders.removeAll(loadersToRemove);
            if(loaders.isEmpty())
                loading = false;
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
    private void setUniform(String uniformName, float value){
        glUniform1f(uniforms.get(uniformName), value);
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
