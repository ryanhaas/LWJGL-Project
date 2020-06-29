package main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture implements Serializable {
    private int id;
    private int width;
    private int height;

    private transient BufferedImage image;

    public Texture(String fileName) {
        this(new File(fileName));
    }

    public Texture(File file) {
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        textureOps();
    }

    public Texture(BufferedImage image) {
        this.image = image;
        textureOps();
    }

    private void textureOps() {
        width = image.getWidth();
        height = image.getHeight();

        ByteBuffer texture = UsefulMethods.getByteBufferFromImage(image);

        id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, texture);
    }

    public void resizeImage(int width, int height) {
        image = UsefulMethods.imageToBufferedImage(image.getScaledInstance(width, height, BufferedImage.SCALE_DEFAULT));
        textureOps();
    }

    public void bind() {
        //binds texture to OpenGL
        glBindTexture(GL_TEXTURE_2D, id);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        ImageIO.write(image, "png", out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image = ImageIO.read(in);
        textureOps();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
