package objects.game;

import main.Texture;
import main.UsefulMethods;
import objects.game.bounds.Bounds;
import objects.game.bounds.TextureBounds;

import java.awt.*;

public class Projectile implements GameObject {
    public static final int DIRECT_LEFT = -1;
    public static final int DIRECT_RIGHT = 1;
    public static final float DEFAULT_SPEED = 10f;
    private int direction;
    private float speed;
    private float x, y, size;
    private Texture texture;
    private TextureBounds textureBounds;
    private Bounds collisionBounds;

    public Projectile(float x, float y, int direction, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.direction = direction;
        this.speed = DEFAULT_SPEED;
        texture = UsefulMethods.getColorTexture(Color.WHITE);
        textureBounds = TextureBounds.defaultTextureBounds();
        collisionBounds = new Bounds(x, y, size, size);
    }

    public Projectile(float x, float y, int direction) {
        this(x, y, direction, 10);
    }

    @Override
    public void render() {
        UsefulMethods.renderQuad(collisionBounds, textureBounds, texture);
    }

    @Override
    public void update() {
        x += speed * direction;
        updateCollisionBounds();
    }

    @Override
    public void updateCollisionBounds() {
        collisionBounds.setBounds(x, y, size, size);
    }

    public void destroy() {
        x = 0;
        y = 0;
        size = 0;
        speed = 0;
        direction = 0;
        texture = null;
        collisionBounds.clear();
    }

    public Bounds getCollisionBounds() {
        return collisionBounds;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
