package objects.game.bounds;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

public class Bounds implements Serializable {
    private static final long serialVersionUID = 1l;

    public float x, y, width, height;
    private Rectangle2D.Float rect;

    public Bounds() {
        if (rect == null)
            rect = new Rectangle2D.Float();
    }

    public Bounds(float x, float y, float width, float height) {
        this();
        setBounds(x, y, width, height);

    }

    public void setBounds(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static Rectangle2D.Float boundsToRect(Bounds bounds) {
        return new Rectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public Rectangle2D.Float boundsToRect() {
        return boundsToRect(this);
    }

    public static void boundsToRect(Bounds bounds, Rectangle2D.Float rect) {
        rect.setRect(boundsToRect(bounds));
    }

    public void clear() {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    @Override
    public String toString() {
        return "Bounds{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
