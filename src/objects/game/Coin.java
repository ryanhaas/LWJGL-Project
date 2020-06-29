package objects.game;

import main.GameEnvironment;
import main.Texture;
import main.UsefulMethods;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import static org.lwjgl.opengl.GL11.*;

public class Coin implements GameObject, Serializable {
    private float x, y, width, height;
    private Rectangle2D.Float collisionBounds;
    private Texture coinTexture;

    public Coin(float x, float y, float size) {
        this.x = x;
        this.y = y;

        coinTexture = new Texture(UsefulMethods.getTextureFile("res/sprites/misc_sprites/coin_64.png"));

        coinTexture.resizeImage((int) size, (int) size);

        this.width = coinTexture.getWidth();
        this.height = coinTexture.getHeight();
        collisionBounds = new Rectangle2D.Float();
        updateCollisionBounds();
    }

    public Coin(float x, float y) {
        this(x, y, 64); //48x48 is the defualt size
    }

    @Override
    public void updateCollisionBounds() {
        collisionBounds.setRect(x, y, width, height);
    }

    @Override
    public void render() {
        if (coinTexture != null)
            coinTexture.bind();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f((int) x, (int) y);
        glTexCoord2f(1, 0);
        glVertex2f((int) (x + width), (int) y);
        glTexCoord2f(1, 1);
        glVertex2f((int) (x + width), (int) (y + height));
        glTexCoord2f(0, 1);
        glVertex2f((int) x, (int) (y + height));
        glEnd();

        if (GameEnvironment.renderBounds) {
            UsefulMethods.getColorTexture(new Color(0, 0, 155, 100)).bind();
            glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2f((int) x, (int) y);
            glTexCoord2f(1, 0);
            glVertex2f((int) (x + width), (int) y);
            glTexCoord2f(1, 1);
            glVertex2f((int) (x + width), (int) (y + height));
            glTexCoord2f(0, 1);
            glVertex2f((int) x, (int) (y + height));
            glEnd();
        }
    }

    @Override
    public void update() {
    }

    public Rectangle2D.Float getCollisionBounds() {
        updateCollisionBounds();
        return collisionBounds;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setLocation(float x, float y) {
        setX(x);
        setY(y);
    }

	public float getX() {
		return x;
	}

    public float getY() {
        return y;
    }
}
