package com.seakernel.stardroid.model;

/**
 * Created by Calvin on 1/17/2018.
 */

public class EnemyShip extends SpaceShip {

    private static final float MAGIC_TIME_SCALAR_FOR_Y_MOVEMENT_IN_A_SINE_WAVE = 5f;
    private long mTime;

    public EnemyShip(float aspectRatio, float v) {
        super(aspectRatio, v);

        mColor = OpenGlColors.ENEMY_RED;

        setCanShoot(false);
        setEngineSpeed(100);
    }

    @Override
    public void draw(float[] mvpMatrix, float dt) {
        mTime += dt / MAGIC_TIME_SCALAR_FOR_Y_MOVEMENT_IN_A_SINE_WAVE;
        float sine = (float) Math.sin(Math.toRadians(mTime));
        moveToPosition(getMoveToX(), mPositionY + sine * 2);

        super.draw(mvpMatrix, dt);
    }
}
