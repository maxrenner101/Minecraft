package me.ghostcodes.io;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public class KeyListener {
    private static boolean[] keys = new boolean[600];

    public static void key_callback(long window, int key, int scancode, int action, int mods){
        keys[key] = (action == GLFW_PRESS || action == GLFW_REPEAT);
    }

    public static boolean isKeyPressed(int key){
        return keys[key];
    }
}
