package objects.game;

import java.awt.geom.Point2D;

public class MovingPlatform extends Platform {
    private Point2D.Float point1;
    private Point2D.Float point2;
    private int direction; //1 to move to point2, -1 to point 2

    private float moveSpeed;
    private float dx, dy;

    public MovingPlatform(float x1, float y1, float x2, float y2, float width, float height, int zIndex) {
        super(x1, y1, width, height, zIndex);
        point1 = new Point2D.Float(x1, y1);
        point2 = new Point2D.Float(x2, y2);
        direction = 1;
        moveSpeed = 2;
    }

    public MovingPlatform(float x1, float y1, float x2, float y2, float width, float height) {
        this(x1, y1, x2, y2, width, height, 0);
    }

    public void update() {
        double rad;
        if (direction == 1)
            rad = Math.atan2(point1.y - getY(), point1.x - getX());
        else
            rad = Math.atan2(point2.y - getY(), point2.x - getX());

        dx = (float) Math.cos(rad) * moveSpeed;
        dy = (float) Math.sin(rad) * moveSpeed;

        if (direction == 1) {
            //if ((int) getX() == (int) point1.x && (int) getY() == (int) point1.y)
            if(betweenPoint(getX(), getY(), point1.x, point1.y))
                direction = -direction;
        } else {
            //if ((int) getX() == (int) point2.x && (int) getY() == (int) point2.y)
            if(betweenPoint(getX(), getY(), point2.x, point2.y))
                direction = -direction;
        }
    }

    public void updateCoords() {
        setLocation(getX() + dx, getY() + dy);
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public float getDx() {
        return dx;
    }

    public float getDy() {
        return dy;
    }

    private boolean betweenPoint(float x1, float y1, float x2, float y2) {
        int margin = 1;
        boolean betweenX = x1 - margin < x2 && x1 + margin > x2;
        boolean betweenY = y1 - margin < y2 && y1 + margin > y2;
        return betweenX && betweenY;
    }
}
