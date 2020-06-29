package saves;

import java.io.Serializable;

public class Progress implements Serializable {
    private int currentLevel;

    public Progress(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
