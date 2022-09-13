package me.ghostcodes.minecraft.engine.io;

import lombok.Getter;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static double xPos = 0, yPos = 0, lastX = 0, lastY = 0;
    @Getter private static double deltaX = 0,deltaY = 0;
    private static final ArrayList<Boolean> buttonsPressed = new ArrayList<>();


    public static void cursorPositionCallback(long window, double xpos, double ypos){
        xPos = xpos;
        yPos = ypos;
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods){
        if(action == GLFW_PRESS){
            buttonsPressed.set(button, true);
        } else if(action == GLFW_RELEASE){
            buttonsPressed.set(button, false);
        }
    }

    public static void handleInput() {
        if(buttonsPressed.size() == 0){
            for(int i = 0; i < 20;i++){
                buttonsPressed.add(false);
            }
        }
        deltaX = xPos-lastX;
        deltaY = yPos-lastY;
        lastX = xPos;
        lastY = yPos;
    }

    public static boolean isButtonPressed(int button){
        return buttonsPressed.get(button);
    }
}
