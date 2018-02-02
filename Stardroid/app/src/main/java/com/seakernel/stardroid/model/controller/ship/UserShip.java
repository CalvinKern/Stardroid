package com.seakernel.stardroid.model.controller.ship;

import com.seakernel.stardroid.model.OpenGlColors;
import com.seakernel.stardroid.model.controller.weapon.Gun;

/**
 * Created by Calvin on 1/21/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */

public class UserShip extends BaseShip {
    private static final float ENGINE_SPEED_PERCENT = 20.0f;

    public UserShip() {
        super();

        setColor(OpenGlColors.PLAYER_BLUE);
        setCanShoot(true);
        setEngineSpeed(ENGINE_SPEED_PERCENT);
        addGun(new Gun(0, getHeight() / 2, 0, null, 1000f));
        addGun(new Gun(0, -getHeight() / 2, 0, null, 1000f));
    }

}
