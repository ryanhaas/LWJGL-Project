package objects.ui;

import main.Texture;
import main.UsefulMethods;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class RoundRectangle implements UIObject {
    private float x, y, width, height;
    private Color color;
    private Texture texture;
    private float arcWidth, arcHeight;

    //WORK IN PROGRESS

    public RoundRectangle(float x, float y, float width, float height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        texture = UsefulMethods.getColorTexture(this.color);
    }

    public void render() {
        glBegin(GL_POLYGON_SMOOTH);
        glTexCoord2f(0,0);
        glVertex2f(x + arcWidth, y);
        glVertex2f(x + width - arcWidth, y);
    }

    public void update() {}

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

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

}
