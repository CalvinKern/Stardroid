package com.seakernel.stardroid.model;

import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 7/20/2017.
 */

public class SpaceShip extends StardroidShape {

    private float MILLISECONDS_BETWEEN_SHOTS = 500f;
    private float mElapsedTime = 0;

    private List<Projectile> mProjectiles = new ArrayList<>();

    @Override
    protected void initialize() {

    }

    @Override
    public void draw(float[] mvpMatrix, float dt) {
//        mMvpMatrix = mvpMatrix.clone();

        drawShooting(mvpMatrix, dt); // TODO: Move this to the engine/model
        Matrix.translateM(mvpMatrix, 0, mPositionX, mPositionY, 0.0f);
//        Log.d("Ship", String.format("doDraw at: (%f,%f)", mPositionX, mPositionY));
//        Log.d("Ship", String.format("doDraw at: (%f,%f)", mvpMatrix[12], mvpMatrix[13]));

    }

    private void drawShooting(float[] mvpMatrix, float dt) {
        mElapsedTime += dt;
        if (mElapsedTime >= MILLISECONDS_BETWEEN_SHOTS) {
            mElapsedTime = 0;
            Projectile shot = new Projectile(mPositionX, mPositionY);
            mProjectiles.add(shot);
        }

        for (Projectile shot : mProjectiles) {
            shot.doDraw(mvpMatrix, dt);
        }

        // TODO: Check for passed bullets
    }

    @Override
    protected float[] getCoordinates() {
        float size = 0.05f;
        return new float[] {
                -size, -size, 0.0f, // bottom left
                size, -size, 0.0f,  // bottom right
                -size,  size, 0.0f, // top left
                size,  size, 0.0f,  // top right
        };
    }

    public float getMoveToX() {
        return mPositionX;
    }

    public float getMoveToY() {
        return mPositionY;
    }

    public void moveToPosition(float x, float y) {
        // TODO: Add in engine speed to position
        mPositionX = x;
        mPositionY = y;
    }
}
