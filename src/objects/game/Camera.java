package objects.game;

import main.GameEnvironment;

public class Camera {
	private float x, y;
	private float delay;

	public Camera(float x, float y) {
		this.x = x;
		this.y = y;
		delay = 0;
	}

	public void updateCamera(Player player) {
		float targetX = player.getX() + player.getWidth()/2 - GameEnvironment.WINDOW_WIDTH/2;
		float targetY = player.getY() + player.getHeight()/2 - GameEnvironment.WINDOW_HEIGHT/2;

		float scale = (float)Math.sqrt(Math.pow(targetX - x, 2) + Math.pow(targetY - y, 2))/5.0f;

		x -= (1.0/GameEnvironment.UPDATE_CAP) * (x - targetX) * scale;
		y -= (1.0/GameEnvironment.UPDATE_CAP) * (y - targetY) * scale;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getDelay() {
		return delay;
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}
}
