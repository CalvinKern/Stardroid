package com.seakernel.stardroid.model.controller.ship;

import com.seakernel.stardroid.model.OpenGlColors;
import com.seakernel.stardroid.model.controller.weapon.Gun;

/**
 * Created by Calvin on 1/21/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */

public class UserShip extends BaseShip {
    private static final float ENGINE_SPEED_PERCENT = 20.0f;
    private static final float SHIP_WIDTH = 0.15f;
    private static final float SHIP_HEIGHT = 0.3f;

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

}
