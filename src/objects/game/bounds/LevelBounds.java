package objects.game.bounds;

import java.io.Serializable;

public class LevelBounds implements Serializable {
    public float gameMinX;
    public float gameMinY;
    public float gameMaxX;
    public float gameMaxY;

    private Bounds bounds;

    public LevelBounds(float gameMinX, float gameMinY, float gameMaxX, float gameMaxY) {
        this.gameMinX = gameMinX;
        this.gameMinY = gameMinY;
        this.gameMaxX = gameMaxX;
        this.gameMaxY = gameMaxY;

        bounds = new Bounds(gameMinX, gameMinY, gameMaxX - gameMinX, gameMaxY - gameMinY);
    }

    public Bounds getBounds() {
        bounds.setBounds(gameMinX, gameMinY, gameMaxX - gameMinX, gameMaxY - gameMinY);
        return bounds;
    }
}
