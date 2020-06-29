package objects.screens;

public class ScreenNotFoundException extends Exception {
    public ScreenNotFoundException() {
        super(ScreenNotFoundException.class.getCanonicalName() + ": Screen not found");
    }

    public ScreenNotFoundException(String message) {
        super(ScreenNotFoundException.class.getCanonicalName() + ": " + message);
    }
}
