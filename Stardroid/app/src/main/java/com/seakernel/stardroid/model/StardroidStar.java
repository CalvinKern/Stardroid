package com.seakernel.stardroid.model;

import android.opengl.Matrix;

/**
 * Created by Calvin on 2/28/16.
 */
public class StardroidStar extends StardroidShape {

    private float mPositionX;
    private float mPositionY;

    private float mFloatingSpeed;
    private static final int MIN_SIZE = 1;
    private static final float HALF_SIZE = 0.0075f;
    private static final float RAND_SIZE_SCALE = 3.5f;
    private static final float SIZE_SCALE = 1000.f;
    private static final float MAX_SIZE_RATIO = (RAND_SIZE_SCALE + MIN_SIZE) / SIZE_SCALE;

    public StardroidStar (float x, float y) {
        super();

        mPositionX = x;
        mPositionY = y;
    }

    @Override
    protected void initialize() {
        super.initialize();

        mColor = getStarColor();
        double rand = Math.random();
        mFloatingSpeed = (float)((rand * RAND_SIZE_SCALE) + MIN_SIZE) / SIZE_SCALE;
    }

    /**
     * Makes the color for the star an applicable star color
     *
     * http://oneminuteastronomer.com/708/star-colors-explained/
     *
     * @return
     */
    private float[] getStarColor() {
        int colorClass = (int)(Math.random() * 15); // Stars range between around 7 colors

        switch (colorClass) {
            case 0:
                return new float[] {0.6f, 0.6f, 1.f, 1.f}; // Blue is the hottest
            case 1:
            case 2:
                return new float[] {0.8f, 0.8f, 1.f, 1.f}; // White Blue is next
            case 3:
            case 4:
                return new float[] {0.875f, 0.875f, 1.f, 1.f}; // White Blue is next
            case 5:
            case 6:
            case 7:
            case 8:
                return new float[] {1.f, 1.f, 1.f, 1.f}; // White
            case 9:
            case 10:
                return new float[] {1.f, 0.95f, 0.7f, 1.f}; // White Yellow
            case 11:
            case 12:
                return new float[] {1.f, 0.75f, 0.5f, 1.f}; // Yellow Orange
            case 13:
            case 14:
                return new float[] {1.f, 0.75f, 0.7f, 1.f}; // Orange Red
            default:
                return new float[] {1.f, 1.f, 1.f, 1.f}; // Return a white star default
        }
    }

    @Override
    public void draw(float[] mvpMatrix) {
        mMVPMatrix = mvpMatrix.clone();

        Matrix.translateM(mMVPMatrix, 0, mPositionX, mPositionY, 0.0f);
//        Matrix.rotateM(mMVPMatrix, 0, 0.0f, 0.0f, 0.0f, 1.0f);

        super.draw(mMVPMatrix);
    }

    @Override
    protected float[] getCoordinates() {
        float size = HALF_SIZE * (mFloatingSpeed / MAX_SIZE_RATIO);
        return new float[] {
                -size, -size, 0.0f, // bottom left
                size, -size, 0.0f,  // bottom right
                -size,  size, 0.0f, // top left
                size,  size, 0.0f,  // top right
        };
    }

    public float getStarFloatingSpeed() {
        return mFloatingSpeed;
    }

    public float getPositionX() {
        return mPositionX;
    }

    public float getPositionY() {
        return mPositionY;
    }

    public void setPositionX(float x) {
        mPositionX = x;
    }
}
