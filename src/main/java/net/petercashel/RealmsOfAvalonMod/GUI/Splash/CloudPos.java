package net.petercashel.RealmsOfAvalonMod.GUI.Splash;

public class CloudPos {
    public float x = 0;
    public float y = 0;

    public CloudPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void tick() {
        x += 0.05f;
    }
}
