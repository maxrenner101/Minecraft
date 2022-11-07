package me.ghostcodes.io;

import lombok.Getter;

import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public class MouseListener {
    @Getter private static double x = 0,y = 0,deltaX = 0,deltaY = 0,lastX = 0,lastY = 0;
    private static final boolean[] buttons = new boolean[12];

    public static void cursor_position_callback(long window, double xpos, double ypos){
        x = xpos;
        y = ypos;
    }

    public static void pollInput(){
        deltaX = x-lastX;
        deltaY = y-lastY;
        lastX = x;
        lastY = y;
    }

    public static boolean isButtonPressed(int button){
        return buttons[button];
    }

    public static void mouse_button_callback(long window, int button, int action, int mods){
        buttons[button] = (action == GLFW_PRESS || action == GLFW_REPEAT);
    }

}
