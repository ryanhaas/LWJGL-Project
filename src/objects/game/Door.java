package objects.game;

import main.Texture;
import main.UsefulMethods;
import objects.game.bounds.Bounds;
import objects.game.bounds.TextureBounds;

import java.io.Serializable;

public class Door implements GameObject, Serializable {
    private float x, y, width, height;
    private Texture texture;
    private TextureBounds textureBounds;
    private Bounds collisionBounds;

    public Door(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        textureBounds = TextureBounds.defaultTextureBounds();
        collisionBounds = new Bounds(x, y, width, height);
        texture = new Texture(UsefulMethods.getTextureFile("res/sprites/room_sprites/door.png"));
        texture.resizeImage((int)width, (int)height);
    }

    public Door(float x, float y) {
        this(x, y, 20, 30);
    }

    @Override
    public void render() {
        UsefulMethods.renderQuad(collisionBounds, textureBounds, texture);
    }

    @Override
    public void update() {

    }

    @Override
    public void updateCollisionBounds() {
        collisionBounds.setBounds(x, y, width, height);
    }

    public Bounds getCollisionBounds() {
        return collisionBounds;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
        updateCollisionBounds();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
