package objects.ui;

public interface UIObject {
    public void render();
    public void update();
    public float getX();
    public float getY();
    public void setLocation(float x, float y);
    public float getWidth();
    public float getHeight();
}
