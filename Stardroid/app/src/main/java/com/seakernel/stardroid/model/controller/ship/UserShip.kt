package com.seakernel.stardroid.model.controller.ship

import android.util.Log
import com.seakernel.stardroid.model.OpenGlColors
import com.seakernel.stardroid.model.StardroidModel
import com.seakernel.stardroid.model.controller.PowerUp
import com.seakernel.stardroid.model.controller.StardroidShape
import com.seakernel.stardroid.model.controller.weapon.Gun
import com.seakernel.stardroid.model.controller.weapon.Projectile.BASE_SPEED_RATE

/**
 * Created by Calvin on 1/21/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */

class UserShip : BaseShip() {

    companion object {
        private const val ENGINE_SPEED_PERCENT = 20.0f
        private const val SHIP_WIDTH = 0.15f
        private const val SHIP_HEIGHT = 0.3f
    }

    // TODO: Need UI to show how long is left
    var powerUpMillisecondsLeft: Float = 0.toFloat()
        private set

    override fun getCoordinates(): FloatArray {
        val left = -SHIP_WIDTH * 1 / 3.0f
        val right = SHIP_WIDTH * 2 / 3.0f

        return floatArrayOf(left, -SHIP_HEIGHT / 2, 0.0f, // bottom left
                right, 0f, 0.0f, // middle right
                left, SHIP_HEIGHT / 2, 0.0f)// top left
    }

    override fun getWidth(): Float {
        return SHIP_WIDTH
    }

    override fun getHeight(): Float {
        return SHIP_HEIGHT
    }

    init {
        color = OpenGlColors.PLAYER_BLUE
        canShoot = true
        engineSpeed = ENGINE_SPEED_PERCENT
        addGun(Gun(width / 4, height / 4, 0f, null))
        addGun(Gun(width / 4, -height / 4, 0f, null))
    }

    fun retrievedPowerUp(shape: StardroidShape) {
        if (shape !is PowerUp) {
            return
        }

        updateSpeeds(shape.getEngineModifier(), shape.getProjectileModifier())

        powerUpMillisecondsLeft = shape.getDurationMilliseconds()
        StardroidModel.getInstance().setPowerUpMillisecondsLeft(powerUpMillisecondsLeft)
    }

    override fun drawChildren(mvpMatrix: FloatArray, dt: Float) {
        super.drawChildren(mvpMatrix, dt)

        if (powerUpMillisecondsLeft > 0 && powerUpMillisecondsLeft.run { powerUpMillisecondsLeft -= dt; powerUpMillisecondsLeft } < 0) {
            updateSpeeds()
        }

        StardroidModel.getInstance().setPowerUpMillisecondsLeft(powerUpMillisecondsLeft)
    }

    private fun updateSpeeds(engineMultiplier: Float = 1.0f, weaponMultiplier: Float = 1.0f) {
        engineSpeed = ENGINE_SPEED_PERCENT * engineMultiplier
        for (gun in guns) {
            gun.setProjectileMultiplier(BASE_SPEED_RATE * weaponMultiplier)
        }
    }
}
