package com.seakernel.stardroid.model

import com.seakernel.stardroid.model.controller.PowerUp
import com.seakernel.stardroid.model.controller.StardroidShape
import com.seakernel.stardroid.model.controller.ship.EnemyShip
import java.util.*

/**
 * Created by Calvin on 1/26/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */
class EnemyController {

    companion object {
        val MILLISECONDS_BETWEEN_POWER_UPS = 10000.0f
        val MIN_MILLISECONDS_BETWEEN_ENEMY_CREATION = 100.0f
    }

    private var mBounds = FloatArray(4)
    private var mEnemies = ArrayList<EnemyShip>()
    private var mPowerUps = ArrayList<PowerUp>()
    private var mElapsedTime: Float = 0.0f
    private var mElapsedPowerUpTime: Float = 0.0f
    private var mMillisecondsBetweenEnemyCreation: Float = 0.0f

    fun resetState() {
        mEnemies.clear()
        mPowerUps.clear()
        mElapsedTime = 0.0f
        setTimeBetweenEnemyCreation(500.0f)
    }

    // left, top, right, bottom
    fun setBounds(bounds: FloatArray) {
        mBounds = bounds
    }

    fun getShapes(): ArrayList<StardroidShape?> {
        return ArrayList(mEnemies + mPowerUps)
    }

    fun destroyShapes(shapes: List<StardroidShape?>) {
        if (mMillisecondsBetweenEnemyCreation > MIN_MILLISECONDS_BETWEEN_ENEMY_CREATION) {
            mMillisecondsBetweenEnemyCreation = Math.max(mMillisecondsBetweenEnemyCreation - shapes.size, MIN_MILLISECONDS_BETWEEN_ENEMY_CREATION)
        }

        mEnemies.removeAll(shapes)
        mPowerUps.removeAll(shapes)
    }

    fun getTimeBetweenEnemyCreation(): Float {
        return mMillisecondsBetweenEnemyCreation
    }

    fun setTimeBetweenEnemyCreation(milliseconds: Float) {
        mMillisecondsBetweenEnemyCreation = Math.max(milliseconds, MIN_MILLISECONDS_BETWEEN_ENEMY_CREATION)
    }

    fun addNewEnemySet(dt: Float) {
        createEnemy(dt)
        createPowerUp(dt)
    }

    private fun getRandomSpawnPosition(): FloatArray {
        return floatArrayOf(mBounds[2], (Math.random() * Math.abs(mBounds[1] - mBounds[3]) - 1).toFloat())
    }

    private fun createPowerUp(dt: Float) {
        synchronized(this, {
            mElapsedPowerUpTime += dt
            if (mElapsedPowerUpTime < MILLISECONDS_BETWEEN_POWER_UPS) {
                return
            }
            mElapsedPowerUpTime = 0f
        })

        val position = getRandomSpawnPosition()
        mPowerUps.add(newRandomPowerUp(position[0], position[1]))
    }

    private fun newRandomPowerUp(x: Float, y: Float): PowerUp {
        val powerUp = PowerUp(x, y)
        powerUp.moveToPosition(mBounds[0] * 2, powerUp.positionY)
        return powerUp
    }

    private fun createEnemy(dt: Float) {
        synchronized(this, {
            mElapsedTime += dt
            if (mElapsedTime < mMillisecondsBetweenEnemyCreation) {
                return
            }
            mElapsedTime = 0f
        })

        val position = getRandomSpawnPosition()
        val ship = newRandomEnemyShip(position[0], position[1])

        mEnemies.add(ship)
    }

    private fun newRandomEnemyShip(x: Float, y: Float): EnemyShip {
        val ship = EnemyShip(x, y)

        setShipProperties(ship)
        ship.moveToPosition(mBounds[0] * 2, ship.positionY)

        return ship
    }

    private fun setShipProperties(ship: EnemyShip) {
        val rand = Math.random()
        when {
            rand < 0.33f -> {
                ship.color = OpenGlColors.ENEMY_RED
                ship.engineSpeed = 5f
            }
            rand < 0.64f -> {
                ship.color = OpenGlColors.ENEMY_RED
                ship.engineSpeed = 7f
                ship.points = 2
            }
            rand < 0.9f -> {
                ship.color = OpenGlColors.ENEMY_RED
                ship.engineSpeed = 10f
                ship.points = 3
            }
            else -> {
                ship.color = OpenGlColors.ENEMY_RED
                ship.engineSpeed = 15f
                ship.points = 5
            }
        }
    }

}
