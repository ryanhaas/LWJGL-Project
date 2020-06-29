package objects.game;

import objects.game.bounds.Bounds;

public interface GameObject {
    public void render();

    public void update();

    public void updateCollisionBounds();
}
