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

public class About implements Screen {
	private GameEnvironment gameEnv;
	private TextRenderer[] text;
	private Button backButton;

	public About(GameEnvironment gameEnv) {
		this.gameEnv = gameEnv;

		String[] about = {
				"A simple platformer game made using Java and LWJGL",
				"for our Honors Seminar class. The goal is to collect",
				"all stars in the level and then proceed to the end of",
				"the level to continue. You can move using WASD, jump with",
				"the space bar, and shoot enemies with C."
		};

		text = new TextRenderer[about.length];

		float y = 150;
		final float spacing = 10;
		for(int i = 0; i < text.length; i++) {
			text[i] = new TextRenderer(0,0, about[i]);
			text[i].setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.PLAIN, 30));
			text[i].setLocation(GameEnvironment.WINDOW_WIDTH/2 - text[i].getWidth()/2, y);
			y += text[i].getHeight() + spacing;
		}

		backButton = new Button(0,0, 150, 50, "Back");
		backButton.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 24f));
		backButton.setLocation(GameEnvironment.WINDOW_WIDTH/2 - backButton.getWidth()/2,
				y + 40);
		backButton.setButtonAction(new ButtonAction() {
			@Override
			public void performAction(Object... args) {
				gameEnv.getScreenManager().moveBackScreen();
			}
		});
	}

	@Override
	public void renderScreen() {
		UsefulMethods.colorScreen(GameMenu.grayBackground);
		for(TextRenderer t : text)
			t.render();
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
		backButton.checkHover(mx, my);
		if(ScreenManager.canPressMouse() && glfwGetMouseButton(gameEnv.getWindow(), GLFW_MOUSE_BUTTON_1) == GLFW_TRUE) {
			backButton.checkPress(mx, my);
			ScreenManager.setMouseLastPressed(System.currentTimeMillis());
		}
	}
}
