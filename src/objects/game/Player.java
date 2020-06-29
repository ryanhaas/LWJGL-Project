package objects.game;

import main.Animation;
import main.GameEnvironment;
import main.Texture;
import main.UsefulMethods;
import objects.game.bounds.Bounds;
import objects.game.bounds.TextureBounds;

import java.awt.*;
import java.io.Serializable;

public class Player implements GameObject, Serializable {
    public static final float DEFAULT_WALK_SPEED = 4;

    //Location variables
    private float x, y, width, height;

    //Bound variables
    private Bounds playerBounds;
    private Bounds collisionBounds;
    private TextureBounds textureBounds;

    //Sprite/Texture variables
    private Texture standing;
    private Texture falling;
    private Texture jumping;
    private Texture currentTexture;
    private Animation walking;
    private Texture collisionTexture;

    //Direction variables
    private float vertSpeed;
    private float horizSpeed;
    private int moveDirect;
    private float walkSpeed;
    public static final float GRAVITY = .3f;
    private float jumpSpeed;
    private int textureDirection;

    private boolean onTopPlat;
    private boolean hitAWall;
    private int health;
    private boolean dead;
    private int projectileDamage;

    public Player(float x, float y) {
        this.x = x;
        this.y = y;

        loadTextures();
        currentTexture = standing;
        textureDirection = 1;
        textureBounds = new TextureBounds();
        jumpSpeed = -6;
        onTopPlat = false;
        health = 100;
        dead = false;
        playerBounds = new Bounds();
        collisionBounds = new Bounds();
        updateTextureBounds();

        width = standing.getWidth();
        height = standing.getHeight();
        projectileDamage = 20;
        walkSpeed = DEFAULT_WALK_SPEED;
    }

    private void loadTextures() {
        //Initializes player textures
        standing = new Texture(UsefulMethods.getTextureFile("res/sprites/player_sprites/idle.png"));
        Texture[] walkingTextures = new Texture[4];
        walkingTextures[0] = new Texture(UsefulMethods.getTextureFile("res/sprites/player_sprites/walk1.png"));
        walkingTextures[1] = new Texture(UsefulMethods.getTextureFile("res/sprites/player_sprites/walk2.png"));
        walkingTextures[2] = new Texture(UsefulMethods.getTextureFile("res/sprites/player_sprites/walk3.png"));
        walkingTextures[3] = new Texture(UsefulMethods.getTextureFile("res/sprites/player_sprites/walk4.png"));
        walking = new Animation(walkingTextures);
        jumping = new Texture(UsefulMethods.getTextureFile("res/sprites/player_sprites/jump.png"));
        falling = new Texture(UsefulMethods.getTextureFile("res/sprites/player_sprites/fall.png"));
        collisionTexture = UsefulMethods.getColorTexture(new Color(255, 255, 0, 150));
    }

    public void update() {
        updateAnimataion();
        updateBounds();
    }

    private void updateBounds() {
        playerBounds.setBounds(x, y, width, height);
        updateCollisionBounds();
    }

    private void updateAnimataion() {
        //animation/sprite change
        if (horizSpeed == 0)
            walking.resetAnimation();
        else
            walking.animate();

        if (vertSpeed == 0 && horizSpeed == 0)
            currentTexture = standing;
        else if (vertSpeed < 0)
            currentTexture = jumping;
        else if (vertSpeed > 0)
            currentTexture = falling;
        else if (horizSpeed != 0)
            currentTexture = walking.getCurrentTexture();
        else ;

        //sets the direction that the player should be looking
        if (horizSpeed > 0 && textureDirection != 1) {
            textureDirection = 1;
            updateTextureBounds();
        } else if (horizSpeed < 0 && textureDirection != -1) {
            textureDirection = -1;
            updateTextureBounds();
        }
    }

    public Bounds getCollisionBounds() {
        updateCollisionBounds();
        return collisionBounds;
    }

    public void updateCollisionBounds() {
        //Sets the collision box to the proper coordinates
        collisionBounds.setBounds(x + width / 2 - 5, y + vertSpeed, width / 2 - 10, height);
    }

    private void updateTextureBounds() {
        //Necessary to set which direction the texture is facing
        //Facing right
        if (textureDirection == 1)
            textureBounds.setBounds(0, 0, 1, 0, 1, 1, 0, 1);
            //Facing left
        else if (textureDirection == -1)
            textureBounds.setBounds(1, 0, 0, 0, 0, 1, 1, 1);
    }

    public void render() {
        //Binds the current player texture to OpenGL
        currentTexture.bind();

        //Renders the player and texture
        UsefulMethods.renderQuad(playerBounds, textureBounds, currentTexture);

        if (GameEnvironment.renderBounds)
            UsefulMethods.renderQuad(collisionBounds, UsefulMethods.STANDARD_TEXTURE_COORDS, collisionTexture);
    }

    public void jump() {
        if (onTopPlat) {
            vertSpeed = jumpSpeed;
            onTopPlat = false;
            updateCoords();
        }
    }

    public void enactGravityOnVertSpeed() {
        vertSpeed += GRAVITY;
    }

    public void retractGravityOnVertSpeed() {
        vertSpeed -= GRAVITY;
    }

    public void updateCoords() {
        updateHoriz();
        updateVert();
        updateBounds();
    }

    public void revertCoords() {
        revertHoriz();
        revertVert();
        updateBounds();
    }

    public void updateHoriz() {
        x += horizSpeed;
    }

    public void revertHoriz() {
        x -= horizSpeed;
    }

    public void updateVert() {
        y += vertSpeed;
    }

    public void revertVert() {
        y -= vertSpeed;
    }

    public void setJumpSpeed(float jumpSpeed) {
        this.jumpSpeed = jumpSpeed;
    }

    public void moveUp(double speed) {
        y -= speed;
    }

    public void moveDown(double speed) {
        y += speed;
    }

    public void moveLeft(boolean b) {
        moveDirect = b ? -1 : 0;
    }

    public void moveRight(boolean b) {
        moveDirect = b ? 1 : 0;
    }

    public void moveHorizStop() {
        moveDirect = 0;
    }

    public void setMoveDirect(int moveDirect) {
        this.moveDirect = moveDirect;
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

    public int moveDirect() {
        return moveDirect;
    }

    //Different than collisionBounds
    public Bounds getPlayerBounds() {
        return playerBounds;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getVertSpeed() {
        return vertSpeed;
    }

    public void setVertSpeed(float vertSpeed) {
        this.vertSpeed = vertSpeed;
    }

    public float getHorizSpeed() {
        return horizSpeed;
    }

    public void setHorizSpeed(float horizSpeed) {
        this.horizSpeed = horizSpeed;
    }

    public float getJumpSpeed() {
        return jumpSpeed;
    }

    public boolean isOnTopPlat() {
        return onTopPlat;
    }

    public void setOnTopPlat(boolean b) {
        onTopPlat = b;
    }

    public int getMoveDirect() {
        return moveDirect;
    }

    public float distanceFromEdgeToCB() {
        return Math.abs(getX() - getCollisionBounds().x);
    }

    public boolean isHitAWall() {
        return hitAWall;
    }

    public void setTextures(Texture standing, Texture falling, Texture jumping, Animation walking) {
        this.standing = standing;
        this.falling = falling;
        this.jumping = jumping;
        this.walking = walking;
    }

    public boolean isFacingRight() {
        return textureDirection>0;
    }

    public boolean isFacingLeft() {
        return textureDirection<0;
    }

    public void setHitAWall(boolean hitAWall) {
        this.hitAWall = hitAWall;
    }

    public void damage(int damage) {
        health = Math.max(0, health-damage);
        dead = health == 0;
    }

    public void putOnPlat(Platform p) {
        setLocation(getX(), p.getY() - getHeight() + 1);
    }

    public boolean isDead() {
        return dead;
    }

    public int getHealth() {
        return health;
    }

    public int getProjectileDamage() {
        return projectileDamage;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public void setProjectileDamage(int projectileDamage) {
        this.projectileDamage = projectileDamage;
    }
}
