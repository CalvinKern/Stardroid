package com.seakernel.stardroid.model.controller.ship;

import com.seakernel.stardroid.model.OpenGlColors;
import com.seakernel.stardroid.model.controller.weapon.Gun;

/**
 * Created by Calvin on 1/21/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */

public class UserShip extends BaseShip {
    private static final float ENGINE_SPEED_PERCENT = 20.0f;


    @Override
    protected float[] getCoordinates() {
        final float size = 0.1f;
        return new float[] {
                -size, -size, 0.0f, // bottom left
                size, -size, 0.0f,  // bottom right
                -size,  size, 0.0f, // top left
                size,  size, 0.0f,  // top right
        };
    }

    public UserShip() {
        super();

        setColor(OpenGlColors.PLAYER_BLUE);
        setCanShoot(true);
        setEngineSpeed(ENGINE_SPEED_PERCENT);
        addGun(new Gun(getWidth() / 2, getHeight() / 6, 0, null, 500.0f));
        addGun(new Gun(getWidth() / 2, -getHeight() / 6, 0, null, 500.0f));
    }

}
