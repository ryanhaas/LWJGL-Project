package objects.screens;

import main.GameEnvironment;
import main.UsefulMethods;
import objects.ui.Button;
import objects.ui.ButtonAction;
import objects.ui.TextRenderer;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;

public class LevelSelect implements Screen {
    private GameEnvironment gameEnv;
	private Button[] levelButtons;
	private TextRenderer moreText;
	private Button backButton;

	public LevelSelect(GameEnvironment gameEnv) {
		this.gameEnv = gameEnv;
		createLevelButtons();
		moreText = new TextRenderer(0,0, "More Levels Coming Soon");
		moreText.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 30f));
		moreText.setLocation(GameEnvironment.WINDOW_WIDTH/2 - moreText.getWidth()/2, GameEnvironment.WINDOW_HEIGHT/2 - moreText.getHeight()/2);
		backButton = new Button(0,0, 150, 50, "Back");
		backButton.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 24f));
		backButton.setLocation(GameEnvironment.WINDOW_WIDTH/2 - backButton.getWidth()/2,
				moreText.getY() + moreText.getHeight() + 20);
		backButton.setButtonAction(new ButtonAction() {
			@Override
			public void performAction(Object... args) {
				gameEnv.getScreenManager().moveBackScreen();
			}
		});
	}

	private void createLevelButtons() {
        levelButtons = new Button[gameEnv.getLevelFiles().length];
        final Font font = GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 22f);
        final float size = 75;
        final float space = 20;
        float collectiveWidth = levelButtons.length * size;
        float x = GameEnvironment.WINDOW_WIDTH/2 - collectiveWidth/2 - (space * levelButtons.length)/2;
        float y = 30;
        for(int i = 0; i < levelButtons.length; i++) {
            final Button b = new Button(x, y, size, size, Integer.toString(i + 1));
            x += space + b.getWidth();
            b.setFont(font);
            b.setEnabled(i+1 <= gameEnv.getProgress());
            b.setButtonAction(new ButtonAction() {
				@Override
				public void performAction(Object... args) {
					int level = Integer.parseInt(b.getText()) - 1;
					try {
						gameEnv.setLevel(level);
					} catch(ArrayIndexOutOfBoundsException e) {
						System.err.println("Level " + level + " does not exist");
					}
				}
			});
            levelButtons[i] = b;
        }
    }

	@Override
	public void renderScreen() {
        UsefulMethods.colorScreen(GameMenu.grayBackground);
        for(Button b : levelButtons)
            b.render();
        moreText.render();
        backButton.render();
	}

	@Override
	public void updateScreen() {

	}

	@Override
	public void checkKeys() {

	}

	@Override
	public void mouseEvents() {
	    float mx = gameEnv.getMouseLoc()[0];
	    float my = gameEnv.getMouseLoc()[1];
        for(Button b : levelButtons)
            b.checkHover(mx, my);
        backButton.checkHover(mx, my);

        if(ScreenManager.canPressMouse() && glfwGetMouseButton(gameEnv.getWindow(), GLFW_MOUSE_BUTTON_1) == GLFW_TRUE) {
        	for(Button b : levelButtons) {
				if (b.checkPress(mx, my))
					break;
			}

			backButton.checkPress(mx, my);

        	ScreenManager.setMouseLastPressed(System.currentTimeMillis());
		}
	}

	public void checkEnabled() {
		for(int i = 0; i < levelButtons.length; i++) {
			levelButtons[i].setEnabled(i+1 <= gameEnv.getProgress());
		}
	}
}
