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

    protected static final float MILLISECONDS_BETWEEN_SHOTS = 500.0f;

    private static final float SIZE = 0.02f;

    private float mElapsedTime;

    private final Collection<Projectile> mProjectiles = new ArrayList<>();

    @Override
    protected void initialize() {
        mColor = OpenGlColors.STAR_YELLOW_ORANGE;
    }

    @Override
    protected void draw(final float[] mvMatrix, final float dt) {
        // TODO: Probably have an offset to move the gun placement on the ship
    }

    @Override
    protected float[] getCoordinates() {
        return new float[] {
                -SIZE, -SIZE, 0.0f, // bottom left
                SIZE, -SIZE, 0.0f,  // bottom right
                -SIZE, SIZE, 0.0f, // top left
                SIZE, SIZE, 0.0f,  // top right
        };
    }

    public void createProjectiles(final float dt, final float positionX, final float positionY) {
        mElapsedTime += dt;
        if (mElapsedTime >= MILLISECONDS_BETWEEN_SHOTS) {
            mElapsedTime = 0;
            final Projectile projectile = new Projectile(positionX, positionY);
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
}
