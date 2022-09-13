package me.ghostcodes.minecraft.engine.io;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {

    private static final ArrayList<Boolean> keysDown = new ArrayList<>();

    public static void keyCallback(long window, int key, int scancode, int action, int mods){
        if(action == GLFW_PRESS){
            keysDown.set(key, true);
        } else if(action == GLFW_RELEASE){
            keysDown.set(key, false);
        }
    }

    public static boolean isKeyPressed(int key){
        if(keysDown.size() == 0){
            for(int i = 0; i < 400;i++){
                keysDown.add(false);
            }
        }
        return keysDown.get(key);
    }
}
