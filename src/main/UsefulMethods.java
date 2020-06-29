package main;

import objects.game.bounds.Bounds;
import objects.game.bounds.TextureBounds;
import org.lwjgl.BufferUtils;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class UsefulMethods {
    public static final TextureBounds STANDARD_TEXTURE_COORDS = new TextureBounds(0, 0, 1, 0, 1, 1, 0, 1);

    /////////////////////////////////////////////////////////
    ////////CAUSES A FATAL JAVA RUNTIME ERROR////////////////
    /////////////////////////////////////////////////////////

    //Color Textures
    /*public static final Texture RED_TEXTURE = getColorTexture(Color.RED);
	public static final Texture BLUE_TEXTURE = getColorTexture(Color.BLUE);
	public static final Texture BLACK_TEXTURE = getColorTexture(Color.BLACK);
	public static final Texture YELLOW_TEXTURE = getColorTexture(Color.YELLOW);
	public static final Texture MAGENTA_TEXTURE = getColorTexture(Color.MAGENTA);
	public static final Texture ORANGE_TEXTURE = getColorTexture(Color.ORANGE);
	public static final Texture GRAY_TEXTURE = getColorTexture(Color.GRAY);*/

    public static ByteBuffer getByteBufferFromImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels_raw = new int[width * height];
        pixels_raw = image.getRGB(0, 0, width, height, null, 0, width);
        ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = pixels_raw[i * width + j];
                pixels.put((byte) ((pixel >> 16) & 0xFF)); //RED
                pixels.put((byte) ((pixel >> 8) & 0xFF));  //GREEN
                pixels.put((byte) ((pixel >> 0) & 0xFF));  //BLUE
                pixels.put((byte) ((pixel >> 24) & 0xFF)); //ALPHA
            }
        }
        pixels.flip();
        return pixels;
    }

    public static File getTextureFile(String loc) {
        return new File(GameEnvironment.class.getClassLoader().getResource(loc).getFile());
    }

    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bi = new BufferedImage
                (im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }

    public static boolean shapeCollision(Shape a, Shape b) {
        Area aa = new Area(a);
        Area bb = new Area(b);
        aa.intersect(bb);
        return !aa.isEmpty();
    }

    public static boolean shapeCollision(Bounds a, Bounds b) {
        return shapeCollision(a.boundsToRect(), b.boundsToRect());
    }

    public static float getSign(float val) {
        if (val >= 0)
            return 1;
        return -1;
    }

    public static Texture getColorTexture(Color color) {
        BufferedImage bi = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        Graphics temp = bi.createGraphics();
        temp.setColor(color);
        temp.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        return new Texture(bi);
    }

    public static void renderQuad(Bounds coords, TextureBounds textureBounds, Texture texture) {
        texture.bind();
        glBegin(GL_QUADS);
        glTexCoord2f(textureBounds.x1, textureBounds.y1);
        glVertex2f((int) coords.x, (int) coords.y);
        glTexCoord2f(textureBounds.x2, textureBounds.y2);
        glVertex2f((int) (coords.x + coords.width), (int) coords.y);
        glTexCoord2f(textureBounds.x3, textureBounds.y3);
        glVertex2f((int) (coords.x + coords.width), (int) (coords.y + coords.height));
        glTexCoord2f(textureBounds.x4, textureBounds.y4);
        glVertex2f((int) coords.x, (int) (coords.y + coords.height));
        glEnd();
    }

    public static void rectToBounds(Rectangle2D.Float rect, Bounds bounds) {
        bounds.setBounds(rect.x, rect.y, rect.width, rect.height);
    }

    public static void colorScreen(Color color) {
        float red = (float)color.getRed()/255;
        float green = (float)color.getGreen()/255;
        float blue = (float)color.getBlue()/255;
        float alpha = (float)color.getAlpha()/255;
        glClearColor(red, green, blue, alpha);
    }
}
