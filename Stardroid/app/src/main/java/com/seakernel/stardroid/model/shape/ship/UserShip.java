package com.seakernel.stardroid.model.shape.ship;

import com.seakernel.stardroid.model.OpenGlColors;
import com.seakernel.stardroid.model.shape.weapon.Gun;

/**
 * Created by Calvin on 1/21/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */

public class UserShip extends BaseShip {
    public static final float ENGINE_SPEED_PERCENT = 20.0f;

    public UserShip() {
        super();

        mColor = OpenGlColors.PLAYER_BLUE;

        setCanShoot(true);
        setEngineSpeed(ENGINE_SPEED_PERCENT);
        addGun(new Gun(0, getHeight() / 2, 0));
        addGun(new Gun(0, -getHeight() / 2, 0));
    }

}
