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

    private List<Projectile> mProjectiles = new ArrayList<>();

    @Override
    protected void initialize() {

    }

    @Override
    public void destroy() {
        destroyProjectiles(mProjectiles);
    }

    @Override
    public void draw(float[] mvpMatrix, float dt) {
//        mMvpMatrix = mvpMatrix.clone();

        Matrix.translateM(mvpMatrix, 0, mPositionX, mPositionY, 0.0f);
//        Log.d("Ship", String.format("doDraw at: (%f,%f)", mPositionX, mPositionY));
//        Log.d("Ship", String.format("doDraw at: (%f,%f)", mvpMatrix[12], mvpMatrix[13]));

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
