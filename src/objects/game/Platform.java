package objects.game;

import main.GameEnvironment;
import main.LevelLoader;
import main.Texture;
import main.UsefulMethods;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;

import static org.lwjgl.opengl.GL11.*;

public class Platform implements GameObject, Serializable {
    private float x, y, width, height;
    private Texture texture;
    private Texture dirtTexture;
    private Texture collisionMaskTexture;
    private Rectangle2D.Float collisionBounds;
    private Rectangle2D.Float top;
    private Rectangle2D.Float bottom;
    private Rectangle2D.Float left;
    private Rectangle2D.Float right;

    private float textureWidth;
    private float textureHeight;

    private boolean updated;

    private int zIndex;

    public Platform(float x, float y, float width, float height, int zIndex) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        updated = false;
        texture = new Texture(UsefulMethods.getTextureFile("res/sprites/room_sprites/grassyDirt.png"));
        dirtTexture = new Texture(UsefulMethods.getTextureFile("res/sprites/room_sprites/dirt.png"));
        collisionMaskTexture = UsefulMethods.getColorTexture(new Color(255, 0, 0, 150));

        collisionBounds = new Rectangle2D.Float();
        top = new Rectangle2D.Float();
        bottom = new Rectangle2D.Float();
        left = new Rectangle2D.Float();
        right = new Rectangle2D.Float();

        if (width < texture.getWidth()) {
            texture.resizeImage((int) width, (int) width);
            dirtTexture.resizeImage((int) width, (int) width);
        }
        if (height < texture.getHeight()) {
            texture.resizeImage((int) height, (int) height);
            dirtTexture.resizeImage((int) height, (int) height);
        }


        textureWidth = width / texture.getWidth();
        textureHeight = height / texture.getHeight();
    }

    public Platform(float x, float y, float width, float height) {
        this(x, y, width, height, 0);
    }

    public void update() {
    }

    public void render() {
        //Renders the platform and its texture
        //Platform texture is repeated as opposed to stretched
        if (height > texture.getHeight()) {
            dirtTexture.bind();
            glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2f((int) x, (int) y);
            glTexCoord2f(textureWidth, 0);
            glVertex2f((int) (x + width), (int) y);
            glTexCoord2f(textureWidth, textureHeight);
            glVertex2f((int) (x + width), (int) (y + height));
            glTexCoord2f(0, textureHeight);
            glVertex2f((int) x, (int) (y + height));
            glEnd();
        }
        texture.bind();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f((int) x, (int) y - 1);
        glTexCoord2f(textureWidth, 0);
        glVertex2f((int) (x + width), (int) y - 1);
        glTexCoord2f(textureWidth, 1);
        glVertex2f((int) (x + width), (int) (y - 1 + texture.getHeight()));
        glTexCoord2f(0, 1);
        glVertex2f((int) x, (int) (y - 1 + texture.getHeight()));
        glEnd();

        if (GameEnvironment.renderBounds) {
            collisionMaskTexture.bind();
            Rectangle2D.Float cb = getCollisionBounds();
            glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2f(cb.x, cb.y);
            glTexCoord2f(1, 0);
            glVertex2f((float) cb.getMaxX(), cb.y);
            glTexCoord2f(1, 1);
            glVertex2f((float) cb.getMaxX(), (float) cb.getMaxY());
            glTexCoord2f(0, 1);
            glVertex2f(cb.x, (float) cb.getMaxY());
            glEnd();
        }
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

    public void updateCollisionBounds() {
        collisionBounds.setRect(x, y, width, height);
    }

    public Rectangle2D.Float getCollisionBounds() {
        updateCollisionBounds();
        return collisionBounds;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Rectangle2D getBounds() {
        return getCollisionBounds();
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setzIndex(int z) {
        zIndex = z;
        LevelLoader.sortPlatforms();
    }

    public int getzIndex() {
        return zIndex;
    }


    public boolean topCollision(Shape a, float buf) {
        top.setRect(getX() + buf, getY(), getWidth() - 2 * buf, buf);
        return UsefulMethods.shapeCollision(top, a);
    }

    public boolean topCollision(Shape a) {
        return topCollision(a, 2f);
    }

    public boolean bottomCollision(Shape a, float buf) {
        bottom.setRect(getX() + buf, getY() + getHeight() - buf, getWidth() - 2 * buf, buf);
        return UsefulMethods.shapeCollision(bottom, a);
    }

    public boolean bottomCollision(Shape a) {
        return bottomCollision(a, 2f);
    }

    public boolean leftCollision(Shape a, float buf) {
        left.setRect(getX(), getY() + buf, buf, getHeight() - 2 * buf); //+1 and -1 to prevent invisible walls
        return UsefulMethods.shapeCollision(left, a);
    }

    public boolean leftCollision(Shape a) {
        return leftCollision(a, 2f);
    }

    public boolean rightCollision(Shape a, float buf) {
        right.setRect(getX() + getWidth() - buf, getY() + buf, buf, getHeight() - 2 * buf); //+1 and -1 to prevent invisible walls
        return UsefulMethods.shapeCollision(right, a);
    }

    public boolean rightCollision(Shape a) {
        return rightCollision(a, 2f);
    }

    public boolean sideCollision(Shape a, float buf) {
        return rightCollision(a, buf) || leftCollision(a, buf);
    }

    public boolean sideCollision(Shape a) {
        return sideCollision(a, 2f);
    }

    public boolean wasUpdated() {
        return updated;
    }

    public void setUpdated(boolean b) {
        updated = b;
    }
}
