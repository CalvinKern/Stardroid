package com.seakernel.stardroid.model.shape.weapon;

import com.seakernel.stardroid.model.OpenGlColors;
import com.seakernel.stardroid.model.shape.StardroidShape;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Calvin on 1/21/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */

public class Gun extends StardroidShape {

    private float mMillisecondsBetweenShots = 500.0f;

    private static final float HALF_SIZE = 0.02f;

    private float mElapsedTime;
    private float[] mTranslationVector = {0.0f, 0.0f, 0.0f};

    private final Collection<Projectile> mProjectiles = new ArrayList<>();

    public Gun() { /* empty constructor */ }

    public Gun(final float x, final float y, final float z) {
        mTranslationVector = new float[]{x, y, z};
    }

    public Gun(final float x, final float y, final float z, final float[] color, final float millisecondsBetweenShots) {
        this(x, y, z);
        setColor(color);
        setMillisecondsBetweenShots(millisecondsBetweenShots);
    }

    @Override
    protected void initialize() {
        mColor = OpenGlColors.STAR_YELLOW_ORANGE;
    }

    @Override
    protected void draw(final float[] mvMatrix, final float dt) {
        android.opengl.Matrix.translateM(mvMatrix, 0, mTranslationVector[0], mTranslationVector[1], mTranslationVector[2]);
    }

    @Override
    protected float[] getCoordinates() {
        return new float[] {
                -HALF_SIZE, -HALF_SIZE, 0.0f, // bottom left
                HALF_SIZE, -HALF_SIZE, 0.0f,  // bottom right
                -HALF_SIZE, HALF_SIZE, 0.0f, // top left
                HALF_SIZE, HALF_SIZE, 0.0f,  // top right
        };
    }

    public void createProjectiles(final float dt, final float positionX, final float positionY) {
        mElapsedTime += dt;
        if (mElapsedTime >= mMillisecondsBetweenShots) {
            mElapsedTime = 0;
            final Projectile projectile = new Projectile(positionX + mTranslationVector[0], positionY + mTranslationVector[1]);
            projectile.setColor(mColor);
            mProjectiles.add(projectile);
        }
    }

    public Collection<Projectile> getProjectiles() {
        return mProjectiles;
    }

    public void destroyProjectiles(final Collection<? extends StardroidShape> projectiles) {
        //noinspection SuspiciousMethodCalls
        mProjectiles.removeAll(projectiles);
    }

    public void setColor(final float[] color) {
        if (color != null) {
            mColor = color;
        }
    }

    public void setMillisecondsBetweenShots(final float milliseconds) {
        if (milliseconds > 0) {
            mMillisecondsBetweenShots = milliseconds;
        }
    }
}
