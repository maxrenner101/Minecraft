package me.ghostcodes.core.engine.io;

import lombok.Getter;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static final boolean[] buttonsPressed = new boolean[30];

    @Getter private static double xPos, yPos, lastXPos, lastYPos, deltaXPos, deltaYPos, displayX = 0, displayY = 0;

    public static void cursor_position_callback(long window, double xpos, double ypos){
        xPos = xpos;
        yPos = ypos;
    }

    public static void handleInput() {
        deltaXPos = xPos - lastXPos;
        deltaYPos = yPos - lastYPos;
        if(xPos > 0 && yPos > 0){
            boolean rotateX = deltaXPos != 0;
            boolean rotateY = deltaYPos != 0;
            if(rotateX)
                displayX = deltaXPos;
            if(rotateY)
                displayY = deltaYPos;
        }
        lastXPos = xPos;
        lastYPos = yPos;
    }

    public static void mouse_button_callback(long window, int button, int action, int mods){
        if(action == GLFW_PRESS) {
            buttonsPressed[button] = true;
        }
        else if(action == GLFW_RELEASE)
            buttonsPressed[button] = false;
    }

    public static boolean isButtonPressed(int mouseButton){
        return buttonsPressed[mouseButton];
    }
}
