package net.petercashel.RealmsOfAvalonMod.GUI.Splash;

import net.minecraft.util.ResourceLocation;

import java.util.Random;

public class MovingTexture {
    public float x = 0;
    public float y = 0;
    public int width = 0;
    public int height = 0;
    public final ResourceLocation texture;
    public static Random random = new Random();

    public boolean BounceMovement = false;
    public boolean MoveRight = false;

    public MovingTexture(float x, float y, int width, int height, ResourceLocation texture) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public float MovementSpeed = 0.05f;

    private void move(float movementSpeed) {
        if (!BounceMovement) {
            x += movementSpeed;
        } else {
            if (MoveRight) {
                x += movementSpeed;
            } else {
                x -= movementSpeed;
            }
            if (x > -1f) {
                //Clamp
                MoveRight = false;
                x = -1f;
            }
            if (x < -100f) {
                //Clamp
                MoveRight = true;
                x = -100f;
            }
        }
    }
    public void tick() {
        //x += MovementSpeed;
        this.move(MovementSpeed);
    }


    public void tickWithRandomSpeedBonux(float bonuxMax) {
        float f = random.nextFloat() / 5f;
        if (f > bonuxMax) {
            f = bonuxMax;
        }
        move(MovementSpeed + f);
    }
}
