package main;

import objects.game.*;
import objects.game.bounds.LevelBounds;
import objects.screens.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import saves.Level;
import saves.Progress;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class GameEnvironment {
	//Window and update rate constants
	public static final int WINDOW_WIDTH = 900;
	public static final int WINDOW_HEIGHT = 680;
	public static final int FPS_CAP = 60;
	public static final int UPDATE_CAP = 60;
	public static boolean renderBounds;

	//Window and game environment variables
	private long window;
	private GLFWVidMode videoMode;
	private boolean isGameRunning;
	private boolean isPaused;
	private static boolean endLoopAndRestart;
	private int currentFps;
	private KeyCallback keyCallback;
	private ScreenManager screenManager;
	private static final Color backgroundColor = new Color(135, 206, 235);

	//Fonts
	private static HashMap<String, Font> fonts;

	//Contains the paths of game and progress files
	private File[] levelFiles;
	private LevelLoader levelLoader;
	public static final File directory = new File(System.getProperty("user.home") + File.separator + "Desktop" +
			"/GameDir");
	private Progress progress;

	private void init() {
		if(!directory.exists())
			directory.mkdirs();

		loadFonts();
		loadProgress();
		loadLevels();

		//Initializes GLFW
		if (!glfwInit())
			throw new IllegalStateException("Couldn't initialize GLFW");

		//Sets window properties
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Game Name TBD", 0, 0);
		if (window == 0)
			throw new IllegalStateException("Couldn't create window");

		//Sets window icon
		try {
			BufferedImage icon = ImageIO.read(getClass().getResourceAsStream("/res/window_icons/icon64.png"));
			GLFWImage glfwImage = GLFWImage.malloc();
			glfwImage.set(icon.getWidth(), icon.getHeight(), UsefulMethods.getByteBufferFromImage(icon));
			GLFWImage.Buffer images = GLFWImage.malloc(1);
			images.put(0, glfwImage);
			glfwSetWindowIcon(window, images);
			images.free();
			glfwImage.free();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Sets window location
		videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (videoMode.width() - WINDOW_WIDTH) / 2, (videoMode.height() - WINDOW_HEIGHT) / 2);
		glfwSetKeyCallback(window, keyCallback = new KeyCallback());
		glfwShowWindow(window);
		glfwMakeContextCurrent(window);

		//Initializes and sets up OpenGL
		GL.createCapabilities();
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

		//For viewport and drawing region
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, WINDOW_WIDTH, WINDOW_HEIGHT, 0, 1, -1);
		glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*// saveDefaultLevel();
		createLevel1("level1");
		createLevel2("level2");
		createLevel3("level3");
		loadLevels();*/

		screenManager = new ScreenManager(keyCallback);
		screenManager.addScreen("menu", new GameMenu(this));
		screenManager.addScreen("level", levelLoader = new LevelLoader(this));
		screenManager.addScreen("settings" , new Settings(this));
		screenManager.addScreen("select", new LevelSelect(this));
		screenManager.addScreen("about", new About(this));

		renderBounds = false;
		isGameRunning = true;
		isPaused = false;
	}


	public ScreenManager getScreenManager() {
		return screenManager;
	}

	public void setScreen(String screenName) {
		try {
			screenManager.setCurrentScreen(screenName);
		} catch (ScreenNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		init();

		//GAME LOOP
		long startTime;
		long lastTime = System.currentTimeMillis();
		long timeMillis;
		int frames = 0;
		long totalTime = 0;
		double updateRate = 1000.0 / UPDATE_CAP;
		double renderRate = 1000.0 / FPS_CAP;
		while (isGameRunning && !endLoopAndRestart) {
			if (!isPaused) {
				startTime = System.currentTimeMillis();

				//Catch up updates if behind
				double timeBehind = startTime - lastTime;
				do {
					update();
					timeBehind -= updateRate;
				}
				while (timeBehind >= updateRate);
				lastTime = startTime;

				//cap render rate
				render();
				timeMillis = System.currentTimeMillis() - startTime;
				long waitTime = (long) (renderRate - timeMillis);
				if (waitTime > 0) {
					try {
						Thread.sleep(waitTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				//for calculating fps
				totalTime += System.currentTimeMillis() - startTime;
				frames++;
				if (frames == FPS_CAP) {
					currentFps = (int) (1000 / (totalTime / (double) frames));
					frames = 0;
					totalTime = 0;
				}

				isGameRunning = !glfwWindowShouldClose(window);
				isPaused = glfwGetWindowAttrib(window, GLFW_FOCUSED) == 0;
			} else {
				//if the game doesn't have focus reduce the work load
				try {
					Thread.sleep(750);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				//Need to poll events or the window will never regain focus
				glfwPollEvents();
				isPaused = glfwGetWindowAttrib(window, GLFW_FOCUSED) == 0;
				if(!isPaused)
					lastTime = System.currentTimeMillis();
			}
		}

		glfwDestroyWindow(window);
		//levelLoader.cleanUp();

		//Will restart and reset the game
		if (endLoopAndRestart) {
			endLoopAndRestart = false;
			run();
		}
	}

	private void update() {
		glfwPollEvents();
		checkKeys();
		if(screenManager != null)
			screenManager.update();
	}

	private void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		UsefulMethods.colorScreen(backgroundColor);

		if (screenManager != null)
			screenManager.render();

		glfwSwapBuffers(window);
	}

	private void checkKeys() {

	}

	public void loadFonts() {
		fonts = new HashMap<String, Font>();
		try {
			//fonts.put("odin rounded", Font.createFont(Font.TRUETYPE_FONT, new File(resPath + "fonts/odin_rounded.ttf")));
			fonts.put("odin rounded", Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/res/fonts/odin_rounded.ttf")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadProgress() {
		File progressFile = new File(directory, "game.progress");
		if(!progressFile.exists()) {
			setProgress(1);
		} else {
			try {
				FileInputStream fis = new FileInputStream(progressFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				progress = (Progress)ois.readObject();
				ois.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setProgress(int level) {
		File progressFile = new File(directory, "game.progress");
		try {
			if(!progressFile.exists())
				progressFile.createNewFile();
			progress = new Progress(level);
			FileOutputStream fos = new FileOutputStream(progressFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(progress);
			oos.close();
			fos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

		if(screenManager != null && screenManager.getAllScreens().containsKey("select"))
			((LevelSelect)screenManager.getScreen("select")).checkEnabled();
	}

	public int getProgress() {
		return progress.getCurrentLevel();
	}

	public void quit() {
		isGameRunning = false;
		System.exit(0);
		endLoopAndRestart = false;
	}

	public static HashMap<String, Font> getFonts() {
		return fonts;
	}

	private void loadLevels() {
		File parent = new File(directory + "/levels/");
		if(!parent.exists())
			parent.mkdirs();

		//need to extract files first
		ArrayList<File> temp = new ArrayList<File>();
		try {
			InputStream zipLoc = getClass().getClassLoader().getResourceAsStream("res/levels.zip");
			if(zipLoc != null) {
				ZipInputStream zipstream = new ZipInputStream(zipLoc);
				byte[] buf = new byte[1024];
				while(true) {
					ZipEntry e = zipstream.getNextEntry();
					if(e == null)
						break;
					if(!e.isDirectory()) {
						File entry = new File(e.getName());
						if(entry.getName().endsWith(".lvl")) {
							File output = new File(parent, entry.getName());
							if(!output.exists())
								output.createNewFile();
							FileOutputStream fos = new FileOutputStream(output);
							int n;
							while((n = zipstream.read(buf, 0, 1024)) > -1)
								fos.write(buf, 0, n);
							fos.close();
							temp.add(output);
						}
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		levelFiles = new File[temp.size()];
		for(int i = 0; i < temp.size(); i++) {
			levelFiles[i] = temp.get(i);
			System.out.println(temp.get(i));
		}

		/*//TEMPORARY
		if(true) {
			ArrayList<File> levels = new ArrayList<File>();
			for(int i = 0; i < directory.listFiles().length; i++) {
				if(directory.listFiles()[i].getName().endsWith(".lvl")) {
					levels.add(directory.listFiles()[i]);
				}
			}
			levels.toArray(levelFiles = new File[levels.size()]);
		}*/
	}

	public void setLevel(int level) {
		levelLoader.loadNewLevel(levelFiles[level]);
		try {
			screenManager.setCurrentScreen("level");
		} catch (ScreenNotFoundException e) {
			e.printStackTrace();
		}
	}

	public long getWindow() {
		return window;
	}

	public static final void saveDefaultLevel() {
		Player player = new Player(30, 0);
		ArrayList<Platform> platforms = new ArrayList<Platform>();
		int height = 30;
		platforms.add(new Platform(10, 200, 200, height));
		platforms.add(new Platform(20, 1000 - 90, 1400 - height, height));
		platforms.add(new MovingPlatform(250, 200, 300, 1000 - 50, 100, height, 1));
		platforms.add(new Platform(600, 1000 - 50 - 200, 200, 200));
		platforms.add(new Platform(400, 1000 - 50 - height - 60, 150, height, 5));
		platforms.add(new MovingPlatform(20, 1000 - 100 - height, 340, 1000 - 100 - height, 100, height));
		platforms.add(new MovingPlatform(450, 1000 - 150 - height, 450, 600 - height, 90, height));

		ArrayList<Enemy> enemies = new ArrayList<Enemy>();
		enemies.add(new Enemy(100, 0));

		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(new Coin(640, 700, 32));
		coins.add(new Coin(680, 700, 32));

		LevelBounds levelBounds = new LevelBounds(-50, -50, 1400, 1000);

		Door door = new Door(0,0, 40, 50);
		door.setLocation(levelBounds.gameMaxX - door.getWidth() - 20,
				platforms.get(platforms.size()-1).getY() - door.getHeight());

		try {
			File f = new File(directory + "/level_file.lvl");
			f.createNewFile();
			System.out.println(f);
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(new Level(player, platforms.toArray(new Platform[platforms.size()]), enemies, coins, levelBounds, null, door));
			oos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public KeyCallback getKeyCallback() {
		return keyCallback;
	}

	public float[] getMouseLoc() {
		DoubleBuffer dbx = BufferUtils.createDoubleBuffer(1);
		DoubleBuffer dby = BufferUtils.createDoubleBuffer(1);
		glfwGetCursorPos(getWindow(), dbx, dby);
		float mx = (float) dbx.get(0);
		float my = (float) dby.get(0);
		return new float[] {mx, my};
	}

	private void createLevel1(String fileName) {
		LevelBounds levelBounds = new LevelBounds(0,0, 3000, 1250);
		ArrayList<Platform> platforms = new ArrayList<Platform>();
		platforms.add(new Platform(0, 400, 950, levelBounds.gameMaxY-400));
		platforms.add(new Platform(975, 360, 200, 30));
		platforms.add(new Platform(1190, 760, levelBounds.gameMaxX-1190, levelBounds.gameMaxY-400));

		Player player = new Player(20, 300);
		ArrayList<Enemy> enemies = new ArrayList<Enemy>();
		Enemy enemy1 = new Enemy(2000, 760);
		enemy1.setAbleToMove(false);
		enemy1.setLocation(enemy1.getX(), enemy1.getY() - enemy1.getHeight());
		enemies.add(enemy1);

		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(new Coin(220, 360, 32));
		coins.add(new Coin(260, 360, 32));
		coins.add(new Coin(300, 360, 32));
		coins.add(new Coin(700, 360, 32));
		coins.add(new Coin(740, 360, 32));
		coins.add(new Coin(780, 360, 32));
		coins.add(new Coin(platforms.get(1).getX() + platforms.get(1).getWidth()/2 - 16, 320, 32));

		Font font = getFonts().get("odin rounded").deriveFont(Font.PLAIN, 30f);
		GameText welcome = new GameText(20, 150, "Welcome!");
		welcome.setFont(font);
		GameText wasdMove = new GameText(100, welcome.getY() + welcome.getHeight(), "Use WASD to Move");
		wasdMove.setFont(font);
		welcome.setLocation(wasdMove.getX() + wasdMove.getWidth()/2 - welcome.getWidth()/2, welcome.getY());
		GameText jumpText = new GameText(0, platforms.get(1).getY() - 150, "Use SPACE to Jump");
		jumpText.setFont(font);
		jumpText.setLocation(platforms.get(1).getX() + platforms.get(1).getWidth()/2 - jumpText.getWidth()/2,
				jumpText.getY());
		GameText enemyText = new GameText(0,0, "Use C to Shoot Enemies");
		enemyText.setFont(font);
		enemyText.setLocation(enemy1.getX() + enemy1.getWidth()/2 - enemyText.getWidth()/2,
				enemy1.getY() - 150);
		GameText[] gameText = {welcome, wasdMove, jumpText, enemyText};

		Door door = new Door(0,0, 40, 50);
		door.setLocation(levelBounds.gameMaxX - door.getWidth() - 20,
				platforms.get(platforms.size()-1).getY() - door.getHeight());

		saveLevel(fileName, new Level(player, platforms.toArray(new Platform[platforms.size()]), enemies, coins, levelBounds, gameText, door));
	}

	private void createLevel2(String fileName) {
		LevelBounds levelBounds = new LevelBounds(0, 0, 1250, 1000);
		ArrayList<Platform> platforms = new ArrayList<Platform>();
		platforms.add(new Platform(0, WINDOW_HEIGHT/2 + 50, levelBounds.getBounds().width, levelBounds.getBounds().height - (WINDOW_HEIGHT/2 + 50)));

		Player player = new Player(20, platforms.get(0).getY() - 150);
		ArrayList<Enemy> enemies = new ArrayList<Enemy>();
		enemies.add(new Enemy(WINDOW_WIDTH + 40, platforms.get(0).getY() - player.getHeight() - 10));
		enemies.add(new Enemy(enemies.get(0).getX() + enemies.get(0).getWidth() + 50, enemies.get(0).getY()));
		enemies.add(new Enemy(enemies.get(1).getX() + enemies.get(1).getWidth() + 50, enemies.get(0).getY()));
		enemies.add(new Enemy(enemies.get(2).getX() + enemies.get(2).getWidth() + 50, enemies.get(0).getY()));
		for(Enemy e : enemies)
			e.setWalkSpeed(2);

		ArrayList<Coin> coins = new ArrayList<Coin>();
        coins.add(new Coin(enemies.get(0).getX() + 50, platforms.get(0).getY() - 40, 32));
        coins.add(new Coin(coins.get(0).getX() + 40, coins.get(0).getY(), 32));
        coins.add(new Coin(coins.get(1).getX() + 40, coins.get(0).getY(), 32));
        coins.add(new Coin(coins.get(2).getX() + 40, coins.get(0).getY(), 32));

        System.out.println(coins.get(0).getX() + "x" + coins.get(0).getY());

		GameText warning = new GameText(0,0, "Don't let the enemies touch you!");
		warning.setFont(getFonts().get("odin rounded").deriveFont(Font.PLAIN, 30f));
		warning.setLocation(WINDOW_WIDTH/2 - warning.getWidth(), player.getY() - 100);
		GameText[] gameText = {warning};

		Door door = new Door(0,0,40,50);
		door.setLocation(levelBounds.gameMaxX - door.getWidth() - 40, platforms.get(0).getY() - door.getHeight());

		saveLevel(fileName, new Level(player, platforms.toArray(new Platform[platforms.size()]), enemies, coins, levelBounds, gameText, door));
	}

	private void createLevel3(String fileName) {
        LevelBounds levelBounds = new LevelBounds(0,0,1000, 1000);
        ArrayList<Platform> platforms = new ArrayList<Platform>();
        platforms.add(new Platform(0, levelBounds.gameMaxY - 150, 400, 150));
        platforms.add(new Platform(levelBounds.gameMaxX - 400, 300, 400, 30));
        platforms.add(new MovingPlatform(415, platforms.get(0).getY(), platforms.get(1).getX()-15-100, platforms.get(1).getY(), 100, 30));

		Player player = new Player(20, platforms.get(0).getY() - 100);
		ArrayList<Enemy> enemies = new ArrayList<Enemy>();

		ArrayList<Coin> coins = new ArrayList<Coin>();
		coins.add(new Coin(360, platforms.get(0).getY() - 40, 32));
		coins.add(new Coin(platforms.get(2).getX() + platforms.get(2).getWidth()/2 - 16, coins.get(0).getY(), 32));
		coins.add(new Coin(platforms.get(1).getX() + 20, platforms.get(1).getY() - 40, 32));

		GameText moveInfo1 = new GameText(0,0, "Use Moving Platforms to move");
		GameText moveInfo2 = new GameText(0,0, "between 2 points");
		moveInfo1.setFont(getFonts().get("odin rounded").deriveFont(Font.PLAIN, 30f));
		moveInfo2.setFont(moveInfo1.getFont());
		moveInfo1.setLocation(50, platforms.get(0).getY() - 200);
		moveInfo2.setLocation(moveInfo1.getX() + moveInfo1.getWidth()/2 - moveInfo2.getWidth()/2, moveInfo1.getY() + moveInfo1.getHeight() + 5);
		GameText[] gameText = {moveInfo1, moveInfo2};

		Door door = new Door(0,0,40,50);
		door.setLocation(levelBounds.gameMaxX - door.getWidth() - 40, platforms.get(1).getY() - door.getHeight());

		saveLevel(fileName, new Level(player, platforms.toArray(new Platform[platforms.size()]), enemies, coins, levelBounds, gameText, door));
    }

    private void createLevel4(String fileName) {
		//LevelBounds
	}

    private void saveLevel(String name, Level level) {
		try {
			File f = new File(directory + "/" + name + ".lvl");
			f.createNewFile();
			System.out.println(f);
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(level);
			oos.close();
			fos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void pauseGameLoop() {
		isPaused = true;
	}

	public void resumeGameLoop() {
		isPaused = false;
	}

	public static void restart() {
		endLoopAndRestart = true;
	}

	public static void main(String[] args) {
		new GameEnvironment().run();
	}

	public File[] getLevelFiles() {
		return levelFiles;
	}
}
