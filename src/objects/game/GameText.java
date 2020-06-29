package objects.game;

import objects.ui.TextRenderer;

import java.io.Serializable;

public class GameText extends TextRenderer implements Serializable {
    private int positionType;
    public static final int POSITION_RELATIVE = 1; //can move in and out of view dependent on player's positionType
    public static final int POSITION_FIXED = 2; //stays in the same positionType on the screen

    public GameText(float x, float y, String text) {
        super(x, y, text);
        positionType = POSITION_RELATIVE;
    }

    public GameText(float x, float y, String text, int positionType) {
        super(x, y, text);
        this.positionType = positionType;
    }

    public void setPositionType(int positionType) {
        this.positionType = positionType;
    }

    public int getPositionType() {
        return positionType;
    }
}
