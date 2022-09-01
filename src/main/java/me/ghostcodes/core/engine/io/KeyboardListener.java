package me.ghostcodes.core.engine.io;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardListener {
    private static final boolean[] keysPressed = new boolean[360];
    private static final boolean[] keysHeld = new boolean[360];

    public static void keyCallback(long window, int key, int scancode, int action, int mods){
        if(action == GLFW_PRESS){
            keysPressed[key] = true;
        } else if(action == GLFW_RELEASE){
            keysPressed[key] = false;
            keysHeld[key] = false;
        } else if(action == GLFW_REPEAT){
            keysHeld[key] = true;
        }
    }

    public static boolean isKeyPressed(int key){
        return keysPressed[key];
    }

    public static boolean isKeyHeld(int key){
        return keysHeld[key];
    }
}
