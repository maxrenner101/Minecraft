package me.ghostcodes.core.minecraft;

import me.ghostcodes.core.engine.Camera;
import me.ghostcodes.core.engine.Engine;
import me.ghostcodes.core.engine.io.KeyboardListener;
import me.ghostcodes.core.engine.io.MouseListener;
import me.ghostcodes.core.engine.util.FunctionDouble;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Minecraft implements FunctionDouble {

    private boolean toggledFullscreen = false;
    @Override
    public void apply(double dt) {
        if(KeyboardListener.isKeyPressed(GLFW_KEY_F11) && !toggledFullscreen){
            toggledFullscreen = true;
            Engine.get().getWindow().toggleFullscreen();
        } else if(!KeyboardListener.isKeyPressed(GLFW_KEY_F11)) toggledFullscreen = false;

        Camera.getRotation().rotateAxis((float)Math.toRadians(MouseListener.getDeltaXPos()*.2), 0, 1, 0);
        Camera.getRotation().rotateLocalX((float)Math.toRadians(MouseListener.getDeltaYPos()*.2));

        Vector3f localZ = Camera.getRotation().transform(new Vector3f(0,0,1));
        Vector3f localX = Camera.getRotation().transform(new Vector3f(1,0,0));

        Camera.setVx(0);
        Camera.setVz(0);

        if(KeyboardListener.isKeyPressed(GLFW_KEY_W)){
            Vector3f adjustment = new Vector3f(localZ.mul(0.02f));

            Camera.setVx(Camera.getVx() + adjustment.x);
            Camera.setVz(Camera.getVz() + adjustment.z);
        }
        if(KeyboardListener.isKeyPressed(GLFW_KEY_S)){
            Vector3f adjustment = new Vector3f(localZ.mul(-0.02f));

            Camera.setVx(Camera.getVx() + adjustment.x);
            Camera.setVz(Camera.getVz() + adjustment.z);
        }
        if(KeyboardListener.isKeyPressed(GLFW_KEY_A)){
            Vector3f adjustment = new Vector3f(localX.mul(-0.02f));

            Camera.setVx(Camera.getVx() + adjustment.x);
            Camera.setVz(Camera.getVz() + adjustment.z);
        }
        if(KeyboardListener.isKeyPressed(GLFW_KEY_D)){
            Vector3f adjustment = new Vector3f(localX.mul(0.02f));

            Camera.setVx(Camera.getVx() + adjustment.x);
            Camera.setVz(Camera.getVz() + adjustment.z);
        }

        Camera.setX(Camera.getVx() + Camera.getX());
        Camera.setZ(Camera.getVz() + Camera.getZ());

    }
}
