package com.seakernel.stardroid.model.controller.ship;

import com.seakernel.stardroid.model.OpenGlColors;
import com.seakernel.stardroid.model.StardroidModel;
import com.seakernel.stardroid.model.controller.PowerUp;
import com.seakernel.stardroid.model.controller.StardroidShape;
import com.seakernel.stardroid.model.controller.weapon.Gun;
import com.seakernel.stardroid.model.controller.weapon.Projectile;

/**
 * Created by Calvin on 1/21/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */

public class UserShip extends BaseShip {
    private static final float ENGINE_SPEED_PERCENT = 20.0f;
    private static final float SHIP_WIDTH = 0.15f;
    private static final float SHIP_HEIGHT = 0.3f;

    private float mPowerUpMillisecondsLeft;

    @Override
    protected float[] getCoordinates() {
        final float left = -SHIP_WIDTH * 1 / 3.0f;
        final float right = SHIP_WIDTH * 2 / 3.0f;

        return new float[] {
                left, -SHIP_HEIGHT / 2, 0.0f, // bottom left
                right, 0, 0.0f,  // middle right
                left,  SHIP_HEIGHT / 2, 0.0f,  // top left
        };
    }

    @Override
    protected float getWidth() {
        return SHIP_WIDTH;
    }

    @Override
    protected float getHeight() {
        return SHIP_HEIGHT;
    }

    public UserShip() {
        super();

        setColor(OpenGlColors.PLAYER_BLUE);
        setCanShoot(true);
        setEngineSpeed(ENGINE_SPEED_PERCENT);
        addGun(new Gun(getWidth() / 4, getHeight() / 4, 0, null, 500.0f));
        addGun(new Gun(getWidth() / 4, -getHeight() / 4, 0, null, 500.0f));
    }

    public void retrievedPowerUp(StardroidShape shape) {
        if (!(shape instanceof PowerUp)) {
            return;
        }

        final PowerUp powerUp = (PowerUp) shape;

        setEngineSpeed(ENGINE_SPEED_PERCENT * powerUp.getEngineModifier());
        for (Gun gun : getGuns()) {
            gun.setProjectileSpeed(Projectile.BASE_SPEED_RATE * powerUp.getProjectileModifier());
        }

        mPowerUpMillisecondsLeft = powerUp.getDurationMilliseconds();
        StardroidModel.getInstance().setPowerUpMillisecondsLeft(mPowerUpMillisecondsLeft);
    }

    // TODO: Need UI to show how long is left
    public float getPowerUpMillisecondsLeft() {
        return mPowerUpMillisecondsLeft;
    }

    @Override
    protected void drawChildren(float[] mvpMatrix, float dt) {
        super.drawChildren(mvpMatrix, dt);

        if (mPowerUpMillisecondsLeft > 0 && (mPowerUpMillisecondsLeft -= dt) < 0) {
            setEngineSpeed(ENGINE_SPEED_PERCENT);
            for (Gun gun : getGuns()) {
                gun.setProjectileSpeed(Projectile.BASE_SPEED_RATE);
            }
        }
        StardroidModel.getInstance().setPowerUpMillisecondsLeft(mPowerUpMillisecondsLeft);
    }
}
