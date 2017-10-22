package com.seakernel.stardroid.model;

import android.opengl.Matrix;

/**
 * Created by Calvin on 2/28/16.
 */
public class StardroidPause extends StardroidShape {

    private static final float POS = 0.9f;
    private static final float WIDTH = .05f;
    private static final float HEIGHT_SCALE = (2f/3f) * (3.5f);

    @Override
    protected void initialize() {
        mColor = new float[] {1.f, 1.f, 1.f, 1.f};
    }

    @Override
    protected float[] getCoordinates() {
        float width = WIDTH;
        float height = WIDTH * HEIGHT_SCALE;
        return new float[] {
                -width, -height, 0.0f, // bottom left
                width, -height, 0.0f,  // bottom right
                -width, height, 0.0f, // top left
                width, height, 0.0f,  // top right
        };
    }

    @Override
    public void draw(float[] mvpMatrix, float dt) {
        mMvpMatrix = new float[mvpMatrix.length];
        // TODO: Instead of modifying mvpMatrix, we could modify COORDINATES (getCoordinates) based on left and right?
        System.arraycopy(mvpMatrix, 0, mMvpMatrix, 0, mvpMatrix.length); // TODO: in place state saving of mvpMatrix for efficiency?

        Matrix.translateM(mMvpMatrix, 0, POS, POS, 0.0f);

        Matrix.translateM(mMvpMatrix, 0, -POS - WIDTH, -POS + (WIDTH * HEIGHT_SCALE), 0.0f);
        super.draw(mMvpMatrix, dt);

        // TODO: Get the right width that should be between the pause bars by considering the eye position?
        Matrix.translateM(mMvpMatrix, 0, 3 * WIDTH, 0.0F, 0.0f);
        super.draw(mMvpMatrix, dt);
    }
}
