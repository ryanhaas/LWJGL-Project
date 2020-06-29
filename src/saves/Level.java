package saves;

import objects.game.*;
import objects.game.bounds.LevelBounds;
import objects.ui.TextRenderer;

import java.io.Serializable;
import java.util.ArrayList;

public class Level implements Serializable {
    private Player player;
    private Platform[] platforms;
    private ArrayList<Enemy> enemies;
    private ArrayList<Coin> coins;
    private LevelBounds levelBounds;
    private GameText[] gameText;
    private Door door;

    public Level(Player player, Platform[] platforms, ArrayList<Enemy> enemies, ArrayList<Coin> coins, LevelBounds levelBounds,
                 GameText[] gameText, Door door) {
        this.player = player;
        this.platforms = platforms;
        this.coins = coins;
        this.enemies = enemies;
        this.levelBounds = levelBounds;
        this.gameText = gameText;
        this.door = door;
    }

    public Player getPlayer() {
        return player;
    }

    public Platform[] getPlatforms() {
        return platforms;
    }

    public ArrayList<Coin> getCoins() {
        return coins;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public LevelBounds getLevelBounds() {
        return levelBounds;
    }

    public GameText[] getGameText() {
        return gameText;
    }

    public Door getDoor() {
        return door;
    }
}
