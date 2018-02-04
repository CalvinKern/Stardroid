package com.seakernel.stardroid.model

import com.seakernel.stardroid.model.controller.StardroidShape
import com.seakernel.stardroid.model.controller.ship.EnemyShip
import java.util.*

/**
 * Created by Calvin on 1/26/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */
class EnemyController {
    @Suppress("PrivatePropertyName", "PropertyName")
    val MIN_MILLISECONDS_BETWEEN_ENEMY_CREATION = 100.0f

    private var mBounds = FloatArray(4)
    private var mEnemies = ArrayList<EnemyShip>()
    private var mElapsedTime: Float = 0.0f
    private var mMillisecondsBetweenEnemyCreation: Float = 0.0f

    fun resetState() {
        mEnemies.clear()
        mElapsedTime = 0.0f
        mMillisecondsBetweenEnemyCreation = 500.0f
    }

    // left, top, right, bottom
    fun setBounds(bounds: FloatArray) {
        mBounds = bounds
    }

    fun getEnemies(): ArrayList<EnemyShip> {
        return ArrayList(mEnemies)
    }

    fun destroyEnemies(enemies: List<StardroidShape?>) {
        if (mMillisecondsBetweenEnemyCreation > MIN_MILLISECONDS_BETWEEN_ENEMY_CREATION) {
            mMillisecondsBetweenEnemyCreation = Math.max(mMillisecondsBetweenEnemyCreation - enemies.size, MIN_MILLISECONDS_BETWEEN_ENEMY_CREATION)
        }
        mEnemies.removeAll(enemies)
    }

    fun getTimeBetweenEnemyCreation(): Float {
        return mMillisecondsBetweenEnemyCreation
    }

    fun setTimeBetweenEnemyCreation(time: Float) {
        mMillisecondsBetweenEnemyCreation = time
    }

    fun addNewEnemySet(dt: Float) {
        createEnemy(dt)
    }

    private fun createEnemy(dt: Float) {
        synchronized(this, {
            mElapsedTime += dt
            if (mElapsedTime < mMillisecondsBetweenEnemyCreation) {
                return
            }
            mElapsedTime = 0f
        })

        val position = floatArrayOf(mBounds[2], (Math.random() * Math.abs(mBounds[1] - mBounds[3]) - 1).toFloat())
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
                ship.color = OpenGlColors.STAR_YELLOW_ORANGE
                ship.engineSpeed = 7f
                ship.points = 2
            }
            rand < 0.9f -> {
                ship.color = OpenGlColors.STAR_WHITE
                ship.engineSpeed = 10f
                ship.points = 3
            }
            else -> {
                ship.color = OpenGlColors.randColor()
                ship.engineSpeed = 15f
                ship.points = 5
            }
        }
    }

}
