package dev.lumin.client.gui.animation;

public class Animation {

    protected float startValue;
    protected float endValue;
    protected float currentValue;

    protected long startTime;
    protected long duration;

    protected Easing easing;
    protected boolean running;
    protected boolean finished;
    protected boolean reverse;

    public Animation(float startValue, float endValue, long duration, Easing easing) {
        this.startValue = startValue;
        this.endValue = endValue;
        this.currentValue = startValue;
        this.duration = duration;
        this.easing = easing;
        this.running = false;
        this.finished = false;
        this.reverse = false;
    }

    public Animation(float startValue, float endValue, long duration) {
        this(startValue, endValue, duration, Easing.EASE_OUT);
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
        this.finished = false;
    }

    public void startReverse() {
        this.reverse = true;
        float temp = this.startValue;
        this.startValue = this.endValue;
        this.endValue = temp;
        start();
    }

    public void reset() {
        this.currentValue = startValue;
        this.running = false;
        this.finished = false;
        this.reverse = false;
    }

    public void update() {
        if (!running || finished) {
            return;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(1f, (float) elapsed / duration);

        float easedProgress = easing.ease(progress);
        currentValue = startValue + (endValue - startValue) * easedProgress;

        if (progress >= 1f) {
            currentValue = endValue;
            finished = true;
            running = false;
        }
    }

    public float getValue() {
        return currentValue;
    }

    public void setValue(float value) {
        this.currentValue = value;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setEasing(Easing easing) {
        this.easing = easing;
    }

    public Easing getEasing() {
        return easing;
    }

    public void setStartValue(float startValue) {
        this.startValue = startValue;
    }

    public void setEndValue(float endValue) {
        this.endValue = endValue;
    }

    public float getProgress() {
        if (!running && finished) return 1f;
        if (!running) return 0f;

        long elapsed = System.currentTimeMillis() - startTime;
        return Math.min(1f, (float) elapsed / duration);
    }

    public float getEasedProgress() {
        return easing.ease(getProgress());
    }
}
