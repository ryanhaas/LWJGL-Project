package objects.game.bounds;

import java.io.Serializable;

public class TextureBounds implements Serializable {
    public float x1, y1;
    public float x2, y2;
    public float x3, y3;
    public float x4, y4;

    public TextureBounds() {
    }

    public TextureBounds(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        setBounds(x1, y1, x2, y2, x3, y3, x4, y4);
    }

    public void setBounds(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.x3 = x3;
        this.y3 = y3;
        this.x4 = x4;
        this.y4 = y4;
    }

    public static TextureBounds defaultTextureBounds() {
        return new TextureBounds(0,0,1,0,1,1,0,1);
    }
}
