package me.ghostcodes.minecraft.game;

import me.ghostcodes.minecraft.engine.Window;
import me.ghostcodes.minecraft.engine.io.KeyListener;
import me.ghostcodes.minecraft.engine.io.MouseListener;
import me.ghostcodes.minecraft.engine.rendering.Camera;
import me.ghostcodes.util.Function;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements Function<Float> {
    @Override
    public void apply(Float dt){
        if(KeyListener.isKeyPressed(GLFW_KEY_F11)){
            if(!Window.get().isToggled()) {
                Window.get().setToggled(true);
                Window.get().toggleFullscreen();
            }
        }else {
            Window.get().setToggled(false);
        }

        Camera.getRotation().rotateAxis((float)Math.toRadians(MouseListener.getDeltaX()*.2), 0, 1, 0);
        Camera.getRotation().rotateLocalX((float)Math.toRadians(MouseListener.getDeltaY()*.2));

        Vector3f localZ = Camera.getRotation().transform(new Vector3f(0,0,1));
        Vector3f localX = Camera.getRotation().transform(new Vector3f(1,0,0));

        Camera.setVx(0);
        Camera.setVz(0);

        if(KeyListener.isKeyPressed(GLFW_KEY_W)){
            Vector3f adjustment = new Vector3f(localZ.mul(1f * dt));

            Camera.setVx(Camera.getVx() + adjustment.x);
            Camera.setVz(Camera.getVz() + adjustment.z);
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_S)){
            Vector3f adjustment = new Vector3f(localZ.mul(-1f * dt));

            Camera.setVx(Camera.getVx() + adjustment.x);
            Camera.setVz(Camera.getVz() + adjustment.z);
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_A)){
            Vector3f adjustment = new Vector3f(localX.mul(-1f * dt));

            Camera.setVx(Camera.getVx() + adjustment.x);
            Camera.setVz(Camera.getVz() + adjustment.z);
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_D)){
            Vector3f adjustment = new Vector3f(localX.mul(1f * dt));

            Camera.setVx(Camera.getVx() + adjustment.x);
            Camera.setVz(Camera.getVz() + adjustment.z);
        }

        Camera.setX(Camera.getVx() + Camera.getX());
        Camera.setZ(Camera.getVz() + Camera.getZ());

    }
}
