package objects.ui;

import main.Texture;
import main.UsefulMethods;
import objects.game.bounds.Bounds;
import objects.game.bounds.TextureBounds;

import java.awt.*;

public class Button implements UIObject {
    private static final Color DEFAULT_IDLE_BG = new Color(100, 100 ,100);
    private static final Color DEFAULT_HOVER_BG = new Color(227, 227, 227);
    private static final Color DEFAULT_IDLE_FG = Color.WHITE;
    private static final Color DEFAULT_HOVER_FG = Color.RED;

    private float x, y, width, height;
    private String text;
    private Font font;
    private Color idleBg;
    private Color idleFg;
    private Color hoverBg;
    private Color hoverFg;
    private Color currentBackground;
    private Color currentForeground;
    private TextRenderer textRenderer;
    private Texture backgroundTexture;

    private ButtonAction buttonAction;

    private Bounds coords;

    private boolean enabled;
    private Texture disabledOverlay;

    public Button(float x, float y, float width, float height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        idleBg = DEFAULT_IDLE_BG;
        idleFg = DEFAULT_IDLE_FG;
        hoverBg = DEFAULT_HOVER_BG;
        hoverFg = DEFAULT_HOVER_FG;
        font = new Font("", Font.PLAIN, 16);
        textRenderer = new TextRenderer(x, y, text);
        dimensionOps();
        textRenderer.setFont(font);
        textRenderer.setTextColor(idleFg);
        backgroundTexture = UsefulMethods.getColorTexture(idleBg);
        coords = new Bounds(this.x, this.y, this.width, this.height);

        enabled = true;
        disabledOverlay = UsefulMethods.getColorTexture(new Color(0,0,0,150));
    }

    private void dimensionOps() {
        this.width = Math.max(textRenderer.getWidth(), width);
        this.height = Math.max(textRenderer.getHeight(), height);
        textRenderer.setLocation(x + this.width/2 - textRenderer.getWidth()/2,
                y + this.height/2 - textRenderer.getHeight()/2);
    }

    public void render() {
        UsefulMethods.renderQuad(coords, UsefulMethods.STANDARD_TEXTURE_COORDS, backgroundTexture);
        textRenderer.render();

        if(!enabled) {
            UsefulMethods.renderQuad(coords, UsefulMethods.STANDARD_TEXTURE_COORDS, disabledOverlay);
        }
    }

    public void update() {}

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

    public String getText() {
        return text;
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
        textRenderer.setLocation(x + this.width/2 - textRenderer.getWidth()/2,
                y + this.height/2 - textRenderer.getHeight()/2);
        coords = new Bounds(this.x, this.y, this.width, this.height);
    }

    public void setText(String text) {
        this.text = text;
        textRenderer.setText(text);
        textRenderer.setLocation(x + this.width/2 - textRenderer.getWidth()/2,
                y + this.height/2 - textRenderer.getHeight()/2);
    }

    public void setFont(Font font) {
        this.font = font;
        textRenderer.setFont(font);
        dimensionOps();
    }

    public boolean intersect(float x, float y) {
        return coords.boundsToRect().contains(x, y);
    }

    public void setIdleBg(Color b) {
        this.idleBg = b;
    }

    public void setIdleFg(Color f) {
        this.idleFg = f;
    }

    public void setHoverBg(Color hoverBg) {
        this.hoverBg = hoverBg;
    }

    public void setHoverFg(Color hoverFg) {
        this.hoverFg = hoverFg;
    }

    private void setCurrentBackground(Color bg) {
        this.currentBackground = bg;
        backgroundTexture = UsefulMethods.getColorTexture(currentBackground);
    }

    private void setCurrentForeground(Color fg) {
        this.currentForeground = fg;
        textRenderer.setTextColor(currentForeground);
    }

    public Color getIdleBg() {
        return idleBg;
    }

    public void setButtonAction(ButtonAction buttonAction) {
        this.buttonAction = buttonAction;
    }

    public void action() {
        if(buttonAction != null)
            buttonAction.performAction();
    }

    public void checkHover(float mouseX, float mouseY) {
        if (enabled && intersect(mouseX, mouseY))
            if (currentBackground != hoverBg) {
                setCurrentBackground(hoverBg);
                setCurrentForeground(hoverFg);
            } else ;
        else if (currentBackground != idleBg) {
            setCurrentBackground(idleBg);
            setCurrentForeground(idleFg);
        }
    }

    public boolean checkPress(float mouseX, float mouseY) {
        if (enabled && intersect(mouseX, mouseY)) {
            action();
            setCurrentBackground(idleBg);
            setCurrentForeground(idleFg);
            return true;
        }
        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
