package objects.screens;

import main.KeyCallback;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.glfw.GLFW.*;

public class ScreenManager {
    private ArrayList<String> screenHistory;
    private HashMap<String, Screen> allScreens;
    private String currentScreen;
    private static long keyLastPressed;
    public static final long KEY_BUFFER = 200;
    private static long mouseLastPressed;
    public static final long MOUSE_BUFFER = 200;
    private KeyCallback keyCallback;

    public ScreenManager(KeyCallback keyCallback) {
    	//allows for moving back screens
        screenHistory = new ArrayList<String>();
        //stores all added screens
        allScreens = new HashMap<String, Screen>();
        //get key event handler from game environment
        this.keyCallback = keyCallback;
    }

    public void render() {
        if(isValidScreen())
            allScreens.get(currentScreen).renderScreen();
    }

    public void update() {
        checkKeys();
        if(isValidScreen()) {
            allScreens.get(currentScreen).updateScreen();
            allScreens.get(currentScreen).checkKeys();
            allScreens.get(currentScreen).mouseEvents();
        }
    }

    public void setCurrentScreen(String screenName) throws ScreenNotFoundException {
    	//sets the current screen to render and update if it exists in the stored screens
        if(allScreens.containsKey(screenName)) {
            currentScreen = screenName;
            screenHistory.add(screenName);
        } else
            throw new ScreenNotFoundException("Screen '" + screenName + "' not found");
    }

    public void addScreen(String screenName, Screen screen) {
    	//add screens to the list if it does not already exists
    	//if it is the first screen added, sets the screen as the current screen
        allScreens.put(screenName, screen);
        if(allScreens.size() == 1) {
            try {
                setCurrentScreen(screenName);
            } catch(ScreenNotFoundException snfe) {
                snfe.printStackTrace();
            }
        }
    }

    public void moveBackScreen() {
        if (!currentScreen.equals("menu")) {
            try {
                //remove the current screen from the history
                screenHistory.remove(screenHistory.size()-1);
                //set the screen to the screen before current then remove the NEW current screen from the history
                setCurrentScreen(screenHistory.get(screenHistory.size() - 1));
                //moving back a screen should not add the previous screen to the history
                //this prevents it without having to add extra parameters in the setCurrentScreen method
                screenHistory.remove(screenHistory.size()-1);
            } catch (ScreenNotFoundException snfe) {
                snfe.printStackTrace();
            }
        }
    }

    public String getCurrentScreen() {
        return currentScreen;
    }

    public HashMap<String, Screen> getAllScreens() {
        return allScreens;
    }

    private boolean isValidScreen() {
    	//returns true if currentScreen exists and allScreens isn't empty
        return !allScreens.isEmpty() && currentScreen != null;
    }

    public void checkKeys() {
        long difTime = 0;
        if (keyCallback.wasAKeyPressed())
            difTime = System.currentTimeMillis() - keyLastPressed;

        //prevents spamming
        if (difTime >= KEY_BUFFER) {
            keyLastPressed = System.currentTimeMillis();
            keyCallback.resetKeyPressed();
            if(keyCallback.getKeyPressed(GLFW_KEY_BACKSPACE) || keyCallback.getKeyPressed(GLFW_KEY_ESCAPE))
                moveBackScreen();
        }
    }

    public static long getKeyLastPressed() {
        return keyLastPressed;
    }

    public static void setKeyLastPressed(long keyLastPressed) {
        ScreenManager.keyLastPressed = keyLastPressed;
    }

    public static boolean canPressKey() {
        return System.currentTimeMillis() - keyLastPressed > KEY_BUFFER;
    }

    public static long getMouseLastPressed() {
        return mouseLastPressed;
    }

    public static void setMouseLastPressed(long mouseLastPressed) {
        ScreenManager.mouseLastPressed = mouseLastPressed;
    }

    public static boolean canPressMouse() {
        return System.currentTimeMillis() - mouseLastPressed > MOUSE_BUFFER;
    }

    public Screen getScreen(String screen) {
        if(allScreens.containsKey(screen))
            return allScreens.get(screen);
        return null;
    }
}
