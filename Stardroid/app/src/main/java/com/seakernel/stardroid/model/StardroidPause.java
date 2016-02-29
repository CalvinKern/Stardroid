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
        super.initialize();

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
    public void draw(float[] mvpMatrix) {
        mMVPMatrix = mvpMatrix.clone();

        Matrix.translateM(mMVPMatrix, 0, POS, POS, 0.0f);

//        super.draw(mMVPMatrix);

        float width = WIDTH/2;
        Matrix.translateM(mMVPMatrix, 0, -POS + width, -POS + (width * HEIGHT_SCALE), 0.0f);
//        super.draw(mMVPMatrix);
    }
}
