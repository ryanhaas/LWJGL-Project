package objects.screens;

import main.GameEnvironment;
import main.UsefulMethods;
import objects.ui.Button;
import objects.ui.ButtonAction;
import objects.ui.TextRenderer;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class GameMenu implements Screen {
    private GameEnvironment gameEnv;
    private ArrayList<TextRenderer> textBoxes;
    private Button[] buttons;

    public static final Color grayBackground = new Color(150, 150, 150);

    public GameMenu(GameEnvironment gameEnv) {
        this.gameEnv = gameEnv;
        createTextBoxes();
        createButtons();
    }

    private void createTextBoxes() {
        textBoxes = new ArrayList<TextRenderer>();
        TextRenderer gameTitle = new TextRenderer(0,0, "Game Name TBD");
        gameTitle.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 85f));
        gameTitle.setLocation(GameEnvironment.WINDOW_WIDTH/2-gameTitle.getWidth()/2, 30);
        textBoxes.add(gameTitle);
        TextRenderer signature = new TextRenderer(0,0,"by Ryan Haas");
        signature.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 12f));
        signature.setLocation(GameEnvironment.WINDOW_WIDTH-signature.getWidth() - 5,
                GameEnvironment.WINDOW_HEIGHT-signature.getHeight()-5);
        textBoxes.add(signature);
    }

    private void createButtons() {
        Font tempFont = GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 32f);
        Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
        attributes.put(TextAttribute.TRACKING, .15);
        float y = textBoxes.get(0).getY() + textBoxes.get(0).getHeight() + 40;
        final float space = 30;
        final float buttonWidth = 350;
        final float buttonHeight = 75;
        final Font buttonFont = tempFont.deriveFont(attributes);
        String[] text = {"Play Game", "Settings", "About", "Exit"};
        ButtonAction[] actions = new ButtonAction[text.length];
        actions[0] = new ButtonAction() {
            @Override
            public void performAction(Object... args) {
                gameEnv.setScreen("select");
            }
        };
        actions[1] = new ButtonAction() {
            @Override
            public void performAction(Object... args) {
                System.out.println("Settings");
                gameEnv.setScreen("settings");
            }
        };
        actions[2] = new ButtonAction() {
            @Override
            public void performAction(Object... args) {
                System.out.println("About");
                gameEnv.setScreen("about");
            }
        };
        actions[3] = new ButtonAction() {
            @Override
            public void performAction(Object... args) {
                gameEnv.quit();
            }
        };
        buttons = new Button[text.length];
        for(int i = 0; i < buttons.length; i++) {
            buttons[i] = new Button(GameEnvironment.WINDOW_WIDTH/2 - buttonWidth/2, y,
                    buttonWidth, buttonHeight, text[i]);
            buttons[i].setFont(buttonFont);
            buttons[i].setButtonAction(actions[i]);
            y += space + buttons[i].getHeight();
        }
    }

    public void renderScreen() {
        UsefulMethods.colorScreen(grayBackground);
        for(TextRenderer text : textBoxes)
            text.render();
        for(Button b : buttons)
            b.render();
    }

    public void updateScreen() {
        //Nothing yet
    }

    @Override
    public void checkKeys() {}

    public void mouseEvents() {
        float mx = gameEnv.getMouseLoc()[0];
        float my = gameEnv.getMouseLoc()[1];
        for (Button b : buttons) {
            b.checkHover(mx, my);
        }

        if (ScreenManager.canPressMouse()) {
            if (glfwGetMouseButton(gameEnv.getWindow(), GLFW_MOUSE_BUTTON_1) == GLFW_TRUE) {
                for (Button b : buttons)
                    if(b.checkPress(mx, my))
                        break;

                ScreenManager.setMouseLastPressed(System.currentTimeMillis());
            }
        }
    }
}
