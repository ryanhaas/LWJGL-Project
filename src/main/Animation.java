package main;

import java.io.Serializable;

public class Animation implements Serializable {
    private int duration; //duration of each texture in frames
    private int frameCount;
    private Texture[] frames;

    public Animation(Texture[] frames) {
        this.frames = frames;
        frameCount = 0;
        duration = 5;
    }

    public void animate() {
        frameCount++;
        if (frameCount >= frames.length * duration)
            frameCount = 0;
    }

    public Texture getCurrentTexture() {
        return frames[frameCount / duration];
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setFrames(Texture[] frames) {
        this.frames = frames;
    }

    public void resetAnimation() {
        frameCount = 0;
    }
}
