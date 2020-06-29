package objects.game;

import main.Animation;
import main.Texture;
import main.UsefulMethods;
import objects.game.bounds.Bounds;
import objects.game.bounds.TextureBounds;

import java.awt.*;

public class Enemy extends Player {
    private static final long serialVersionUID = 1l;

    private int frameCount;
    private Texture colorGreen;
    private Texture colorYellow;
    private Texture colorRed;
    private Texture currentHealthColor;
    private Texture colorGray;
    private Bounds currentHealthBounds; //only the current health
    private Bounds healthBounds; //the entire health bar including gray
    private TextureBounds textureBounds;

    private boolean randomizedMovement;
    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_STOP = 3;
    private int direction;

    private boolean ableToMove;

    public Enemy(float x, float y) {
        super(x, y);
        loadTextures();
        randomizedMovement = false;
        ableToMove = true;
        currentHealthColor = colorGreen;
        healthBounds = new Bounds(x + 10, y - 30, getWidth()-10, 20);
        textureBounds = TextureBounds.defaultTextureBounds();
        updateCurrentHealthBounds();
    }

    private void updateCurrentHealthBounds() {
        float percentHealth = getHealth()/100.0f;
        float newWidth = (healthBounds.width - 10) * percentHealth; //minus 10 for border
        if(currentHealthBounds == null)
            currentHealthBounds = new Bounds(healthBounds.x + 5, healthBounds.y + 5, newWidth, healthBounds.height - 10);
        else
            currentHealthBounds.width = newWidth;
    }

    private void loadTextures() {
        Texture standing = new Texture(UsefulMethods.getTextureFile("res/sprites/enemy_sprites/idle.png"));
        Texture[] walkingTextures = new Texture[4];
        walkingTextures[0] = new Texture(UsefulMethods.getTextureFile("res/sprites/enemy_sprites/walk1.png"));
        walkingTextures[1] = new Texture(UsefulMethods.getTextureFile("res/sprites/enemy_sprites/walk2.png"));
        walkingTextures[2] = new Texture(UsefulMethods.getTextureFile("res/sprites/enemy_sprites/walk3.png"));
        walkingTextures[3] = new Texture(UsefulMethods.getTextureFile("res/sprites/enemy_sprites/walk4.png"));
        Animation walking = new Animation(walkingTextures);
        Texture jumping = new Texture(UsefulMethods.getTextureFile("res/sprites/enemy_sprites/jump.png"));
        Texture falling = new Texture(UsefulMethods.getTextureFile("res/sprites/enemy_sprites/fall.png"));
        setTextures(standing, falling, jumping, walking);

        colorGreen = UsefulMethods.getColorTexture(Color.GREEN);
        colorYellow = UsefulMethods.getColorTexture(Color.YELLOW);
        colorRed = UsefulMethods.getColorTexture(Color.RED);
        colorGray = UsefulMethods.getColorTexture(new Color(0,0,0,160));
    }

    public void randomMoveHoriz() {
        double rand = Math.random();
        if (rand < .45)
            moveLeft(true);
        else if (rand < .90)
            moveRight(true);
        else
            moveHorizStop();
    }

    public void update() {
        super.update();
        frameCount++;
        if(ableToMove) {
            if (randomizedMovement && frameCount == 150) {
                randomMoveHoriz();
                frameCount = 0;
            } else if (!randomizedMovement) {
                if (direction == DIRECTION_LEFT)
                    moveLeft(true);
                else if (direction == DIRECTION_RIGHT)
                    moveRight(true);
                else if (direction == DIRECTION_STOP)
                    moveHorizStop();
                else
                    direction = DIRECTION_STOP;
            }

            if (isHitAWall() && randomizedMovement) {
                double rand = Math.random();
                if (rand < .5)
                    jump();
                else
                    setMoveDirect(-getMoveDirect());
            } else if (!randomizedMovement && isHitAWall()) {
                jump();
            }
        }
    }

    public void updateCoords() {
        super.updateCoords();
        healthBounds.x = getX() + 10;
        healthBounds.y = getY() - 30;
        currentHealthBounds.x = healthBounds.x + 5;
        currentHealthBounds.y = healthBounds.y + 5;
    }

    public void render() {
        super.render();
        if(getHealth() < 100)
            drawHealth();
    }

    private void drawHealth() {
        UsefulMethods.renderQuad(healthBounds, textureBounds, colorGray);
        UsefulMethods.renderQuad(currentHealthBounds, textureBounds, currentHealthColor);
    }

    public void damage(int damage) {
        super.damage(damage);
        if(getHealth() > 66);
        else if(getHealth() > 33)
            currentHealthColor = colorYellow;
        else
            currentHealthColor = colorRed;
        updateCurrentHealthBounds();
    }

    public void setRandomizedMovement(boolean b) {
        randomizedMovement = b;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isRandomizedMovement() {
        return randomizedMovement;
    }

    public boolean isAbleToMove() {
        return ableToMove;
    }

    public void setAbleToMove(boolean ableToMove) {
        this.ableToMove = ableToMove;
    }
}
