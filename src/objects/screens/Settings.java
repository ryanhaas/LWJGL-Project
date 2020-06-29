package objects.screens;

import main.GameEnvironment;
import main.UsefulMethods;
import objects.ui.Button;
import objects.ui.ButtonAction;
import objects.ui.TextRenderer;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Settings implements Screen {
	private GameEnvironment gameEnv;
	private TextRenderer textRenderer;
	private Button backButton;
	
	public Settings(GameEnvironment gameEnv) {
		this.gameEnv = gameEnv;
		textRenderer = new TextRenderer(0,0,"Settings will be available in future versions");
		textRenderer.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.PLAIN, 30));
		textRenderer.setLocation(GameEnvironment.WINDOW_WIDTH/2 - textRenderer.getWidth()/2,
				GameEnvironment.WINDOW_HEIGHT/2 - textRenderer.getHeight()/2 - 25);

		backButton = new Button(0,0, 150, 50, "Back");
		backButton.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 24f));
		backButton.setLocation(GameEnvironment.WINDOW_WIDTH/2 - backButton.getWidth()/2,
				textRenderer.getY() + textRenderer.getHeight() + 20);
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
		textRenderer.render();
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
