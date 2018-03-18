package com.seakernel.stardroid.model.controller

import com.seakernel.stardroid.model.OpenGlColors
import com.seakernel.stardroid.model.controller.ship.BaseShip

/**
 * Created by Calvin on 2/18/2018.
 */
class PowerUp(x: Float, y: Float) : BaseShip(x, y) {
    private var mTime: Long = 0

    init {
        color = OpenGlColors.POWER_UP_GREEN
    }

    override fun draw(mvpMatrix: FloatArray, dt: Float) {
        mTime += (dt / MAGIC_TIME_SCALAR_FOR_Y_MOVEMENT_IN_A_SINE_WAVE).toLong()
        val sine = Math.sin(Math.toRadians(mTime.toDouble())).toFloat()
        moveToPosition(moveToX, mPositionY + sine * 2)

        color[3] = (sine + 1) / 4 + 0.25f // Change the alpha to change from [0.25f, 0.75f] to have some extra way of denoting that it's special
        super.draw(mvpMatrix, dt)
    }

    fun getEngineModifier(): Float {
        return 100f
    }

    fun getProjectileModifier(): Float {
        return 10f
    }
    fun getDurationMilliseconds(): Float {
        return 7000.0f
    }

    companion object {
        private val MAGIC_TIME_SCALAR_FOR_Y_MOVEMENT_IN_A_SINE_WAVE = 5f
    }
}