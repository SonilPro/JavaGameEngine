package core;

import launcher.Launcher;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {

    private final Vector2d previousPos, currentPos;
    private final Vector2f displacementVec;

    private boolean inWindow = false, leftButtonPress = false, rightButtonPress = false;

    public boolean erase = true;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displacementVec = new Vector2f();
    }

    public void init() {
        GLFW.glfwSetCursorPosCallback(Launcher.getWindow().getWindow(), (window, xPos, yPos) -> {
            currentPos.x = xPos;
            currentPos.y = yPos;
        });

        GLFW.glfwSetCursorEnterCallback(Launcher.getWindow().getWindow(), (window, entered) -> {
            inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(Launcher.getWindow().getWindow(), (window, button, action, mode) -> {
            leftButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonPress = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });
    }

    public void input() {
        if (erase) {
            displacementVec.x = 0;
            displacementVec.y = 0;
        }
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double x = currentPos.x - previousPos.x;
            double y = currentPos.y - previousPos.y;
            boolean rotateX = x != 0;
            boolean rotateY = y != 0;
            if (rotateX)
                displacementVec.y = (float) x;
            if (rotateY)
                displacementVec.x = (float) y;
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;

    }

    public Vector2f getDisplacementVec() {
        return displacementVec;
    }

    public boolean isLeftButtonPress() {
        return leftButtonPress;
    }

    public boolean isRightButtonPress() {
        return rightButtonPress;
    }
}
