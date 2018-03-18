package com.seakernel.stardroid.model.controller.ship;

import android.opengl.Matrix;
import android.util.Log;

import com.seakernel.stardroid.model.controller.StardroidShape;
import com.seakernel.stardroid.model.controller.effect.Explosion;
import com.seakernel.stardroid.model.controller.weapon.Gun;
import com.seakernel.stardroid.model.controller.weapon.Projectile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Calvin on 7/20/2017.
 */

public class BaseShip extends StardroidShape {

    private final float MAX_SPEED_ENGINE = 0.01f;
    private final float MIN_SPEED_ENGINE = 0.001f;

    private float mMoveToX;
    private float mMoveToY;
    private float mSpeedPercent;
    private boolean mCanShoot;
    private int mHealthPoints;
    private int mHealthPointsMax;
    private int mPoints;

    private final Collection<Gun> mGuns = new ArrayList<>();

    public BaseShip() {
        super();

        mPoints = 1;
        mHealthPoints = 1;
        mHealthPointsMax = 1;
    }

    public BaseShip(float x, float y) {
        this();

        mPositionX = x;
        mPositionY = y;
    }

    @Override
    protected void initialize() {
        mMoveToX = mPositionX;
        mMoveToY = mPositionY;
        resetEngineSpeed();
    }

    @Override
    public void destroy() {
        for (final Gun gun : mGuns) {
            gun.destroy();
        }
    }

    @Override
    public void draw(float[] mvpMatrix, float dt) {
        final float dx = mMoveToX - mPositionX;
        final float dy = mMoveToY - mPositionY;
        final float distance = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        final float step = Math.min(getRawSpeed() * dt, distance);

        if (mMoveToX != mPositionX) {
            mPositionX += step * (dx / distance);
        }

        if (mMoveToY != mPositionY) {
            mPositionY += step * (dy / distance);
        }

        Matrix.translateM(mvpMatrix, 0, mPositionX, mPositionY, 0.0f);
    }

    @Override
    protected void drawChildren(final float[] mvpMatrix, final float dt) {
        drawGuns(mvpMatrix, dt);
    }

    @Override
    protected float[] getCoordinates() {
        final float size = 0.05f;
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

    protected void addGun(final Gun gun) {
        mGuns.add(gun);
    }

    protected List<Gun> getGuns() {
        return new ArrayList<>(mGuns);
    }

    public List<Projectile> getProjectiles() {
        final List<Projectile> projectiles = new ArrayList<>();
        for (final Gun gun : mGuns) {
            projectiles.addAll(gun.getProjectiles());
        }
        return projectiles;
    }

    public void destroyProjectiles(final Collection<? extends StardroidShape> projectiles) {
        // TODO: restructure this (destroyProjectiles) and getProjectiles to not have to loop
        //       through every gun,
        for (final Gun gun : mGuns) {
            gun.destroyProjectiles(projectiles);
        }
    }

    private void drawGuns(final float[] mvpMatrix, final float dt) {
        for (final Gun gun : mGuns) {
            gun.doDraw(mvpMatrix, dt);

            if (mCanShoot) {
                gun.createProjectiles(dt, mPositionX, mPositionY);
            }
        }
    }

    /**
     * @return the engine speed as a percent [0, 100]
     */
    public float getEngineSpeed() {
        return mSpeedPercent;
    }

    private float normSpeed() {
        return 1 / (MAX_SPEED_ENGINE - MIN_SPEED_ENGINE) * (getRawSpeed() - MIN_SPEED_ENGINE);
//        return (mSpeedPercent - MIN_SPEED_ENGINE) / (MAX_SPEED_ENGINE - MIN_SPEED_ENGINE);
    }

    public float getRawSpeed() {
        return getRawSpeed(mSpeedPercent);
    }

    public float getRawSpeed(float speed) {
        return speed / 100 * MAX_SPEED_ENGINE;
    }

    public boolean setEngineSpeed(float speedPercent) {
//        float speed = speedPercent / 100 * normSpeed();
//        if (speed <= MAX_SPEED_ENGINE && speed >= MIN_SPEED_ENGINE) {
//            mSpeedPercent = speed;
//            return true;
//        }
        mSpeedPercent = speedPercent;
        return false;
    }

    public void resetEngineSpeed() {
        mSpeedPercent = 10;
//        Log.d("SpaceShip", String.format("Reset Engine Speed (%f)", mSpeedPercent));
    }

    public boolean incrementEngineSpeed() {
        if (getRawSpeed() < MAX_SPEED_ENGINE) {
            mSpeedPercent += 10;
            Log.d("SpaceShip", String.format("New Engine Speed (%f)", mSpeedPercent));
            return true;
        }
        return false;
    }

    public void setCanShoot(boolean canShoot) {
        mCanShoot = canShoot;
    }

    public boolean getCanShoot() {
        return mCanShoot;
    }

    /**
     * @return true if the ship no longer has health
     */
    public Explosion shipHit() {
        mHealthPoints -= 1;
        if (mHealthPoints <= 0) {
            return new Explosion(this);
        }
        return null;
    }

    public void setHealthPointsMax(int max) {
        mHealthPointsMax = max;
    }

    public void setHealthPoints(int health) {
        mHealthPoints = health;
    }

    public int getHealthPoints() {
        return mHealthPoints;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(final int points) {
        mPoints = points;
    }

}
