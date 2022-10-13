package me.ghostcodes.minecraft;

import lombok.Getter;
import me.ghostcodes.io.KeyListener;
import me.ghostcodes.io.MouseListener;
import me.ghostcodes.minecraft.world.Chunk;
import me.ghostcodes.minecraft.world.World;
import me.ghostcodes.minecraft.world.WorldGenerator;
import me.ghostcodes.minecraft.world.WorldType;
import me.ghostcodes.rendering.Camera;
import org.joml.Vector3f;

import java.util.function.Function;

import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements Function<Double, Void> {

    @Getter private final World world;

    private final Camera camera;

    private boolean isFullscreen;
    private final Function<Boolean, Void> fullscreenFunction;

    public Minecraft(Function<Boolean, Void> fullscreenFunction){
        this.fullscreenFunction = fullscreenFunction;
        camera = Camera.get();
        world = WorldGenerator.createWorld(WorldType.FLAT);
        world.loadChunksAroundPos((int) (-camera.getX()/Chunk.WIDTH) * Chunk.WIDTH, (int) (-camera.getZ()/Chunk.LENGTH) * Chunk.LENGTH);
    }
    private boolean isFullscreenToggled = false;

    @Override
    public Void apply(Double dt) {
        if(KeyListener.isKeyPressed(GLFW_KEY_F11)){
            if(!isFullscreenToggled) {
                isFullscreenToggled = true;
                isFullscreen = !isFullscreen;
                fullscreenFunction.apply(isFullscreen);
            }
        } else {
            isFullscreenToggled = false;
        }


        camera.getRotation().rotateAxis((float)Math.toRadians(MouseListener.getDeltaX()*.2), 0, 1, 0);
        camera.getRotation().rotateLocalX((float)Math.toRadians(MouseListener.getDeltaY()*.2));

        Vector3f localZ = camera.getRotation().transform(new Vector3f(0,0,1));
        Vector3f localX = camera.getRotation().transform(new Vector3f(1,0,0));

        camera.setVx(0);
        camera.setVz(0);

        if(KeyListener.isKeyPressed(GLFW_KEY_W)){
            Vector3f adjustment = new Vector3f(localZ.mul((float) (3f * dt)));

            camera.setVx(camera.getVx() + adjustment.x);
            camera.setVz(camera.getVz() + adjustment.z);
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_S)){
            Vector3f adjustment = new Vector3f(localZ.mul((float) (-3f * dt)));

            camera.setVx(camera.getVx() + adjustment.x);
            camera.setVz(camera.getVz() + adjustment.z);
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_A)){
            Vector3f adjustment = new Vector3f(localX.mul((float) (-3f * dt)));

            camera.setVx(camera.getVx() + adjustment.x);
            camera.setVz(camera.getVz() + adjustment.z);
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_D)){
            Vector3f adjustment = new Vector3f(localX.mul((float) (3f * dt)));

            camera.setVx(camera.getVx() + adjustment.x);
            camera.setVz(camera.getVz() + adjustment.z);
        }

        if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)){
            camera.setY(camera.getY()+(float)(3f*dt));
        }

        camera.setX(camera.getVx() + camera.getX());
        camera.setZ(camera.getVz() + camera.getZ());
        return null;
    }
}
