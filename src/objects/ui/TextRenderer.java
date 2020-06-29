package objects.ui;

import main.Texture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import static org.lwjgl.opengl.GL11.*;

public class TextRenderer implements UIObject, Serializable {
    //Properties
    private String text;
    private float x, y;
    private float width, height;
    private Color textColor;
    private Font font;

    //For rendering
    private static Graphics graphics; //Only to calculate width and height for BufferedImage
    private FontMetrics fontMetrics;
    private Texture texture;

    public TextRenderer(float x, float y, String text) {
        this.x = x;
        this.y = y;
        this.text = text;

        //Initializes default properties
        textColor = Color.BLACK;
        font = new Font("default", Font.PLAIN, 12);
        if (graphics == null)
            graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();

        createTexture();
    }

    public void render() {
        texture.bind();
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f((int) x, (int) y);
        glTexCoord2f(1, 0);
        glVertex2f((int) (x + width), y);
        glTexCoord2f(1, 1);
        glVertex2f((int) (x + width), (int) (y + height));
        glTexCoord2f(0, 1);
        glVertex2f((int) x, y + height);
        glEnd();
    }

    public void update() {}

    //Creates and returns texture using a BufferedImage
    private void createTexture() {
        updateGraphics();

        //Sets the width and height of the buffered image based on size of the text
        width = fontMetrics.stringWidth(text);
        height = fontMetrics.getHeight();
        BufferedImage bufferedImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D imageGraphics = bufferedImage.createGraphics();

        //Sets graphics properties
        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        imageGraphics.setColor(textColor);
        imageGraphics.setFont(font);

        //Draws the string on the image
        imageGraphics.drawString(text, 0, fontMetrics.getAscent()); //need to push text down to draw in top left

        texture = new Texture(bufferedImage);
    }

    //Necessary to update font metrics
    private void updateGraphics() {
        graphics.setFont(font);
        fontMetrics = graphics.getFontMetrics();
    }

    //Getters and Setters
    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        createTexture();
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        createTexture();
    }

    public void setText(String text) {
        this.text = text;
        createTexture();
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

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
