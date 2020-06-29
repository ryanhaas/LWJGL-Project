package main;

import objects.game.*;
import objects.game.bounds.Bounds;
import objects.game.bounds.LevelBounds;
import objects.game.bounds.TextureBounds;
import objects.screens.Screen;
import objects.screens.ScreenManager;
import objects.screens.ScreenNotFoundException;
import objects.ui.Button;
import objects.ui.TextRenderer;
import saves.Level;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.ArrayList;

import static main.GameEnvironment.WINDOW_HEIGHT;
import static main.GameEnvironment.WINDOW_WIDTH;
import static main.GameEnvironment.renderBounds;
import static main.UsefulMethods.shapeCollision;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.opengl.GL11.*;

public class LevelLoader implements Screen {
	//Level info
	private GameEnvironment gameEnv;
	private Level currentLevel;
	private File currentLevelFile;
	private boolean firstLoad;
	private boolean finishedGame;

	private int score;

	//Game Objects
	private Camera camera;
	private Player player;
	private static Platform[] platforms;
	private ArrayList<Enemy> enemies;
	private ArrayList<Coin> coins;
	private ArrayList<Projectile> projectiles;
	private GameText[] gameText;
	private Door doorToNextLevel;

	//Variables for collision
	//The rectangles help save memory
	private Platform[] prevPlats; //prevPlats[0] will always be dedicated to the player
	private Rectangle2D.Float platBounds;
	private Rectangle2D.Float playCB; //player collision bounds

	//UI Objects
	private TextRenderer textRenderer;
	private TextRenderer beatGame;
	private Button returnToMenu;
	private Texture grayOverlay;
	//Health Info
	private Texture healthBackground;
	private Texture currentHealth;
	private static final Texture HEALTH_GREEN = UsefulMethods.getColorTexture(Color.GREEN);
	private static final Texture HEALTH_YELLOW = UsefulMethods.getColorTexture(Color.YELLOW);
	private static final Texture HEALTH_RED = UsefulMethods.getColorTexture(Color.RED);
	private static final TextureBounds healthTextureBounds = TextureBounds.defaultTextureBounds();
	private Bounds healthBounds;
	private Bounds currentHealthBounds;
	private TextRenderer healthText;

	//Game size variables
	private LevelBounds levelBounds;

	private long keyLastPressed;

	public LevelLoader(GameEnvironment gameEnv) {
		this.gameEnv = gameEnv;
		firstLoad = true;
		ops();
	}

	//Use either file or preloaded Level object
	public LevelLoader(GameEnvironment gameEnv, File levelFile) {
		this.gameEnv = gameEnv;
		firstLoad = true;
		if(!loadNewLevel(levelFile))
			gameEnv.getScreenManager().moveBackScreen();
		ops();
	}

	private void ops() {
		camera = new Camera(0, 0);
		camera.setDelay(.5f);
		finishedGame =  false;
		beatGame = new TextRenderer(0, 0, "Congratulations! You beat the game!");
		beatGame.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 30f));
		beatGame.setLocation(GameEnvironment.WINDOW_WIDTH/2 - beatGame.getWidth()/2, GameEnvironment.WINDOW_HEIGHT/2 - beatGame.getHeight()/2 - 10);
		beatGame.setTextColor(Color.WHITE);
		returnToMenu = new Button(0, 0, 250, 50, "Return to Menu");
		returnToMenu.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 30f));
		returnToMenu.setLocation(GameEnvironment.WINDOW_WIDTH/2 - returnToMenu.getWidth()/2,
				beatGame.getY() + beatGame.getHeight() + 20);
		grayOverlay = UsefulMethods.getColorTexture(new Color(0,0,0,150));
	}

	public boolean loadNewLevel(File levelFile) {
		if (!firstLoad && currentLevel != null)
			cleanUpLevel();
		//gets level from file
		try {
			FileInputStream fis = new FileInputStream(levelFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			currentLevel = (Level) ois.readObject();
			currentLevelFile = levelFile;
			player = currentLevel.getPlayer();
			platforms = currentLevel.getPlatforms();
			sortPlatforms();
			enemies = currentLevel.getEnemies();
			levelBounds = currentLevel.getLevelBounds();
			coins = currentLevel.getCoins();
			projectiles = new ArrayList<Projectile>();
			gameText = currentLevel.getGameText();
			doorToNextLevel = currentLevel.getDoor();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		textRenderer = new TextRenderer(5, 5, "Score: " + score);
		textRenderer.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 36f));

		createHealthBar();

		prevPlats = new Platform[1 + enemies.size()];
		platBounds = new Rectangle2D.Float();
		playCB = new Rectangle2D.Float();
		return true;
	}

	private void createHealthBar() {
		healthBackground = UsefulMethods.getColorTexture(new Color(0,0,0,160));
		currentHealth = HEALTH_GREEN;
		final float healthWidth = 250;
		healthBounds = new Bounds(WINDOW_WIDTH - healthWidth - 10, 10, healthWidth, 45);
        currentHealthBounds = new Bounds(healthBounds.x + 5, healthBounds.y + 5, healthWidth-10, healthBounds.height-10);
        healthText = new TextRenderer(0,0, "HP: " + player.getHealth());
        healthText.setTextColor(Color.WHITE);
        healthText.setFont(GameEnvironment.getFonts().get("odin rounded").deriveFont(Font.BOLD, 22f));
	    healthText.setLocation(healthBounds.x + healthBounds.width - healthText.getWidth() - 10,
                healthBounds.y + healthBounds.height/2 - healthText.getHeight()/2);
	}

	//mostly to change the width of currentHealthBounds
    private void updateCurrentHealth() {
        float percentHealth = player.getHealth()/100.0f;
        float newWidth = (healthBounds.width - 10) * percentHealth; //minus 10 for border
        if(currentHealthBounds == null)
            currentHealthBounds = new Bounds(healthBounds.x + 5, healthBounds.y + 5, newWidth, healthBounds.height - 10);
        else
            currentHealthBounds.width = newWidth;

        if(player.getHealth() > 66);
        else if(player.getHealth() > 33)
            currentHealth = HEALTH_YELLOW;
        else
            currentHealth = HEALTH_RED;

        healthText.setText("HP: " + player.getHealth());
        healthText.setLocation(healthBounds.x + healthBounds.width - healthText.getWidth() - 10,
                healthBounds.y + healthBounds.height/2 - healthText.getHeight()/2);
    }

	public void updateScreen() {
		if(!finishedGame) {
			player.update();
			player.setHorizSpeed(player.getMoveDirect() * player.getWalkSpeed());
			for (Enemy e : enemies) {
				e.update();
				if (!e.isRandomizedMovement()) {
					if (e.getX() > player.getX())
						e.setDirection(Enemy.DIRECTION_LEFT);
					else if (e.getX() < player.getX())
						e.setDirection(Enemy.DIRECTION_RIGHT);
					else
						e.setDirection(Enemy.DIRECTION_STOP);

					if (shapeCollision(player.getCollisionBounds(), e.getCollisionBounds())) {
						restartLevel();
					}
				}
				e.setHorizSpeed(e.getMoveDirect() * e.getWalkSpeed());
			}

			collisionChecks();

			for (int i = 0; i < projectiles.size(); i++) {
				projectiles.get(i).update();
				Rectangle2D projectile = projectiles.get(i).getCollisionBounds().boundsToRect();
				boolean stop = false;
				for (int j = 0; j < platforms.length; j++) {
					if (shapeCollision(projectile, platforms[j].getCollisionBounds())) {
						projectiles.get(i).destroy();
						projectiles.remove(i);
						i--;
						stop = true;
						break;
					}
				}

				if (!stop) {
					for (int j = 0; j < enemies.size(); j++) {
						if (shapeCollision(projectile, enemies.get(j).getCollisionBounds().boundsToRect())) {
							projectiles.get(i).destroy();
							projectiles.remove(i);
							enemies.get(j).damage(player.getProjectileDamage());
							if (enemies.get(j).getHealth() <= 0) {
								enemies.remove(j);
								increaseScore(1);
							}
							i--;
							stop = true;
							break;
						}
					}
					if (!stop) {
						if (projectiles.get(i).getX() > levelBounds.gameMaxX || projectiles.get(i).getX() < levelBounds.gameMinX) {
							projectiles.get(i).destroy();
							projectiles.remove(i);
							i--;
						}
					}
				}
			}

			for (int i = 0; i < coins.size(); i++) {
				Coin c = coins.get(i);
				c.update();
				if (shapeCollision(c.getCollisionBounds(), player.getPlayerBounds().boundsToRect())) {
					increaseScore(1);
					coins.remove(i);
					i--;
				}
			}

			if (player.getX() < levelBounds.gameMinX - player.getWidth() - 20 || player.getX() > levelBounds.gameMaxX + 20 ||
					player.getY() < levelBounds.gameMinY - player.getHeight() - 20 || player.getY() > levelBounds.gameMaxY + 20)
				restartLevel();

			camera.updateCamera(player);
		}
	}

	private void increaseScore(int increment) {
		score += increment;
		textRenderer.setText("Score: " + score);
	}

	@Override
	public void mouseEvents() {
		float mx = gameEnv.getMouseLoc()[0];
		float my = gameEnv.getMouseLoc()[1];
		if(finishedGame) {
			returnToMenu.checkHover(mx, my);
			if(ScreenManager.canPressMouse() && glfwGetMouseButton(gameEnv.getWindow(), GLFW_MOUSE_BUTTON_1) == GLFW_TRUE) {
				if(returnToMenu.checkPress(mx, my)) {
					try {
						gameEnv.getScreenManager().setCurrentScreen("menu");
						finishedGame = false;
					} catch (ScreenNotFoundException e) {
						e.printStackTrace();
					}
				}

				ScreenManager.setMouseLastPressed(System.currentTimeMillis());
			}
		}
	}

	private void collisionChecks() {
		Player[] playerAndEnemies = new Player[1 + enemies.size()];
		playerAndEnemies[0] = player;
		for (int i = 0; i < enemies.size(); i++)
			playerAndEnemies[i + 1] = enemies.get(i);

		//NOTE: BOTTOM COLLISION APPEARS TO BE BROKEN
		//THERE IS ALSO A BUG WHEN YOU COLLIDE IN THE VERY CORNERS OF THE PLATFORM
		for (int i = 0; i < playerAndEnemies.length; i++) {
			Player pObj = playerAndEnemies[i];
			Platform prevPlat = prevPlats[i];
			if (prevPlat != null) {
				pObj.updateCoords();
				playCB.setRect(pObj.getCollisionBounds().boundsToRect());
				pObj.revertCoords();
				platBounds.setRect(prevPlat.getCollisionBounds());

				boolean setNull = true;
				boolean updateCoords = false;

				if (shapeCollision(platBounds, playCB) && prevPlat.topCollision(playCB) && !prevPlat.sideCollision(playCB)
						&& !prevPlat.bottomCollision(playCB) && playCB.getCenterY() < prevPlat.getY()) {
					if(!prevPlat.wasUpdated()) {
						prevPlat.update();
						updateCoords = true;
						prevPlat.setUpdated(true);
					}
					float dx = 0;
					if (prevPlat instanceof MovingPlatform) {
						dx = ((MovingPlatform) prevPlat).getDx();
						if(updateCoords)
							((MovingPlatform) prevPlat).updateCoords();
					}

					pObj.setOnTopPlat(true);
					pObj.setVertSpeed(0);
					//for some reason enemy instances need a an extra pixel buffer
					int buffer = (pObj instanceof  Enemy) ? 2 : 1;
					pObj.setLocation(pObj.getX() + dx, prevPlat.getY() - pObj.getHeight() + buffer);
					setNull = false;
				}

				if (setNull) {
					pObj.setOnTopPlat(false);
					prevPlats[i] = null;
					prevPlat = null;
				}
			}

			for (Platform p : platforms) {
				if (p != prevPlat) {
					boolean updateCoords = false;

					if (!p.wasUpdated()) {
						p.update();
						updateCoords = true;
						p.setUpdated(true);
					}

					float dx = 0;
					if (p instanceof MovingPlatform) {
						if (updateCoords)
							((MovingPlatform) p).updateCoords();
						dx = ((MovingPlatform) p).getDx();
					}

					platBounds.setRect(p.getCollisionBounds());
					//vertical
					pObj.updateVert();
					playCB.setRect(pObj.getCollisionBounds().boundsToRect());
					pObj.revertVert();
					if (shapeCollision(playCB, platBounds)) {
						if (p.topCollision(playCB) && !p.sideCollision(playCB) && !p.bottomCollision(playCB) && playCB.getCenterY() < p.getY()) {
							pObj.setOnTopPlat(true);
							pObj.setVertSpeed(0);
							pObj.setLocation(pObj.getX() + dx, p.getY() - pObj.getHeight() + 1);
							prevPlats[i] = p;
						} else if (p.bottomCollision(playCB) && pObj.getY() > p.getY() + p.getHeight()) {
							pObj.setOnTopPlat(false);
							pObj.setVertSpeed(3);
							prevPlats[i] = null;
						}
					}

					//horizontal
					pObj.updateHoriz();
					playCB.setRect(pObj.getCollisionBounds().boundsToRect());
					pObj.revertHoriz();
					if (shapeCollision(playCB, platBounds)) {
						pObj.setHitAWall(false);
						if (p.leftCollision(playCB)) {
							pObj.setLocation(p.getX() - pObj.getWidth() + dx + pObj.distanceFromEdgeToCB() - Math.abs(pObj.getHorizSpeed()), pObj.getY());
							pObj.setHorizSpeed(0);
							pObj.setHitAWall(true);
						} else if (p.rightCollision(playCB)) {
							pObj.setLocation(p.getX() + p.getWidth() + dx - pObj.distanceFromEdgeToCB() + Math.abs(pObj.getHorizSpeed()) - 1, pObj.getY());
							pObj.setHorizSpeed(0);
							pObj.setHitAWall(true);
						}
					}
				}
			}

			if (!pObj.isOnTopPlat())
				pObj.enactGravityOnVertSpeed();
			pObj.updateCoords();
		}

		if(doorToNextLevel != null && shapeCollision(doorToNextLevel.getCollisionBounds(), player.getCollisionBounds())) {
			System.out.println("go to next level or win screen");
			File[] levels = gameEnv.getLevelFiles();
			for(int i = 0; i < levels.length; i++) {
				if(levels[i] == currentLevelFile) {
					if(i + 1 < levels.length) {
						loadNewLevel(levels[i + 1]);
						System.out.println(gameEnv.getProgress());
						gameEnv.setProgress(gameEnv.getProgress() + 1);
						System.out.println(gameEnv.getProgress());
						break;
					} else {
						finishedGame = true;
					}
				}
			}
		}

		for(Platform p : platforms)
			p.setUpdated(false);
	}

	public void renderScreen() {
		glPushMatrix();

		if (camera.getX() < levelBounds.gameMinX)
			camera.setX(levelBounds.gameMinX);
		if (camera.getX() + WINDOW_WIDTH > levelBounds.gameMaxX)
			camera.setX(levelBounds.gameMaxX - WINDOW_WIDTH);
		if (camera.getY() < levelBounds.gameMinY)
			camera.setY(levelBounds.gameMinY);
		if (camera.getY() + WINDOW_HEIGHT > levelBounds.gameMaxY)
			camera.setY(levelBounds.gameMaxY - WINDOW_HEIGHT);

		glTranslatef(-camera.getX(), -camera.getY(), 0);

		for (Platform p : platforms)
			p.render();
		for (Coin c : coins)
			c.render();
		for (Enemy e : enemies)
			e.render();
		for(Projectile p : projectiles)
			p.render();
		if(gameText != null)
			for(GameText gt : gameText)
				if(gt.getPositionType() == GameText.POSITION_RELATIVE)
					gt.render();
		if(doorToNextLevel != null)
			doorToNextLevel.render();
		player.render();

		glTranslatef(camera.getX(), camera.getY(), 0);

		glPopMatrix();

		//Render text after popping matrix if you want the location of the text locked on the screen
		//Won't abide to tracking
		if(gameText != null)
			for(GameText gt : gameText)
				if(gt.getPositionType() == GameText.POSITION_FIXED)
					gt.render();

		textRenderer.render();

        UsefulMethods.renderQuad(healthBounds, healthTextureBounds, healthBackground);
        UsefulMethods.renderQuad(currentHealthBounds, healthTextureBounds, currentHealth);
        healthText.render();

        if(finishedGame) {
			UsefulMethods.renderQuad(levelBounds.getBounds(), UsefulMethods.STANDARD_TEXTURE_COORDS, grayOverlay);
			beatGame.render();
			returnToMenu.render();
		}
	}

	public void checkKeys() {
		final long keyBuffer = 200;
		long difTime = 0;
		if (gameEnv.getKeyCallback().wasAKeyPressed()) {
			difTime = System.currentTimeMillis() - keyLastPressed;
		}

		if (gameEnv.getKeyCallback().getKeyPressed(GLFW_KEY_A))
			player.moveLeft(true);
		else if (player.moveDirect() != 1)
			player.moveLeft(false);
		if (gameEnv.getKeyCallback().getKeyPressed(GLFW_KEY_D))
			player.moveRight(true);
		else if (player.moveDirect() != -1)
			player.moveRight(false);

		if (gameEnv.getKeyCallback().getKeyPressed(GLFW_KEY_SPACE))
			player.jump();

		//prevents the spamming
		if (difTime >= keyBuffer) {
			keyLastPressed = System.currentTimeMillis();
			gameEnv.getKeyCallback().resetKeyPressed();
			if(gameEnv.getKeyCallback().getKeyPressed(GLFW_KEY_C)) {
				float centerX = player.getX() + player.getWidth()/2 - 5;
				float centerY = player.getY() + player.getHeight()/2 - 5;
				int direction = (player.isFacingRight()) ? Projectile.DIRECT_RIGHT : Projectile.DIRECT_LEFT;
				projectiles.add(new Projectile(centerX, centerY, direction));
			}
		}
	}

	public static void sortPlatforms() {
		//Sorts platforms based on zIndex using insertion sort
		if (platforms != null) {
			Platform temp;
			for (int i = 0; i < platforms.length; i++) {
				for (int j = i; j > 0; j--) {
					if (platforms[j].getzIndex() < platforms[j - 1].getzIndex()) {
						temp = platforms[j];
						platforms[j] = platforms[j - 1];
						platforms[j - 1] = temp;
					}
				}
			}
		}
	}

	public void cleanUpLevel() {
		platforms = null;
		coins.clear();
		coins = null;
		prevPlats = null;
		platBounds = null;
		player = null;
		score = 0;
		keyLastPressed = 0;
		projectiles.clear();
		projectiles = null;
		System.gc();
	}

	public void restartLevel() {
		cleanUpLevel();
		loadNewLevel(currentLevelFile);
	}
}
