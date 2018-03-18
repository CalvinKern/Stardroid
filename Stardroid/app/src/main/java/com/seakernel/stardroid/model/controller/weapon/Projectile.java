package com.seakernel.stardroid.model.controller.weapon;

import android.opengl.Matrix;

import com.seakernel.stardroid.model.controller.StardroidShape;

/**
 * Created by Calvin on 8/28/2017.
 */

public class Projectile extends StardroidShape {

    public static final float BASE_SPEED_RATE = 0.001f;
    private float mFloatingSpeed = BASE_SPEED_RATE;

    public Projectile(float x, float y) {
        setPositionX(x);
        setPositionY(y);
    }

    public Projectile(float x, float y, float speed) {
        this(x, y);
        setSpeed(speed);
    }

    @Override
    protected void initialize() {

    }

    @Override
    public void draw(float[] mvpMatrix, float dt) {
        update(dt);

        Matrix.translateM(mvpMatrix, 0, mPositionX, mPositionY, 0.0f);
    }

    private void update(float dt) {
        // Fixed time step
//        setPositionX(getPositionX() -(getStarFloatingSpeed() * 10));

        // Variable time step
        setPositionX(mPositionX + (mFloatingSpeed * dt));
    }

    @Override
    protected float[] getCoordinates() {
        float size = 0.01f;
        return new float[] {
                -size * 5, -size, 0.0f, // bottom left
                size * 5, -size, 0.0f,  // bottom right
                -size * 5,  size, 0.0f, // top left
                size * 5,  size, 0.0f,  // top right
        };
    }

    public void setPositionX(float x) {
        mPositionX = x;
    }

    public void setPositionY(float y) {
        mPositionY = y;
    }

    public void setSpeed(float speed) {
        mFloatingSpeed = speed;
    }
}
