package com.seakernel.stardroid.model;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Calvin on 7/20/2017.
 */

public class SpaceShip extends StardroidShape {

    private float mMillisecondsBetweenShots = 500f;
    private float mElapsedTime = 0;
    private float mMoveToX;
    private float mMoveToY;
    private float mSpeed = 0.001f;

    private List<Projectile> mProjectiles = new ArrayList<>();

    @Override
    protected void initialize() {
        mMoveToX = mPositionX;
        mMoveToY = mPositionY;
    }

    @Override
    public void destroy() {
        destroyProjectiles(mProjectiles);
    }

    @Override
    public void draw(float[] mvpMatrix, float dt) {
        final float step = mSpeed * dt;

        if (mMoveToX != mPositionX) {
            mPositionX += (step * (mMoveToX > mPositionX ? 1 : -1)) % (mMoveToX - mPositionX);
        }

        if (mMoveToY != mPositionY) {
            mPositionY += (step * (mMoveToY > mPositionY ? 1 : -1)) % (mMoveToY - mPositionY);
        }

        Matrix.translateM(mvpMatrix, 0, mPositionX, mPositionY, 0.0f);
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
        return mMoveToX;
    }

    public float getMoveToY() {
        return mMoveToY;
    }

    public void moveToPosition(float x, float y) {
        mMoveToX = x;
        mMoveToY = y;
    }

    public float getMillisecondsBetweenShots() {
        return mMillisecondsBetweenShots;
    }

    public void setMillisecondsBetweenShots(float milliseconds) {
        mMillisecondsBetweenShots = milliseconds;
    }

    public void createProjectiles(float dt) {
        mElapsedTime += dt;
        if (mElapsedTime >= mMillisecondsBetweenShots) {
            mElapsedTime = 0;
            Projectile projectile = new Projectile(mPositionX, mPositionY);
            mProjectiles.add(projectile);
        }
    }

    public List<Projectile> getProjectiles() {
        return new ArrayList<>(mProjectiles);
    }

    public void destroyProjectiles(List<? extends StardroidShape> stardroidShapes) {
        //noinspection SuspiciousMethodCalls
        mProjectiles.removeAll(stardroidShapes);
    }
}
