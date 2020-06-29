package main;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyCallback extends GLFWKeyCallback {
    private static boolean[] keys;
    private boolean wasAKeyPressed;
    private int action;

    public KeyCallback() {
        keys = new boolean[65535];
    }

    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        keys[key] = action != GLFW_RELEASE;
        this.action = action;
        wasAKeyPressed = true;
    }

    public boolean wasAKeyPressed() {
        for (boolean b : keys)
            if (b == true)
                return true;
        return false;
    }

    public void resetKeyPressed() {
        wasAKeyPressed = false;
    }

    public boolean getKeyPressed(int key) {
        return key < keys.length && keys[key];
    }
}
