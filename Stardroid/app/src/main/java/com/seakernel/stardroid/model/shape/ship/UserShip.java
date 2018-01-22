package com.seakernel.stardroid.model.shape.ship;

import com.seakernel.stardroid.model.OpenGlColors;

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
    }

}
