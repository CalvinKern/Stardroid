package com.seakernel.stardroid.model

import com.seakernel.stardroid.model.controller.StardroidShape
import com.seakernel.stardroid.model.controller.ship.EnemyShip
import java.util.*

/**
 * Created by Calvin on 1/26/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */
class EnemyController {

    private var mBounds = FloatArray(4)
    private var mEnemies = ArrayList<EnemyShip>()
    private var mElapsedTime: Float = 0.0f
    private var mMillisecondsBetweenEnemyCreation: Float = 1500.0f

    fun resetState() {
        mEnemies.clear()
        mElapsedTime = 0.0f
        mMillisecondsBetweenEnemyCreation = 1500.0f
    }

    // left, top, right, bottom
    fun setBounds(bounds: FloatArray) {
        mBounds = bounds
    }

    fun getEnemies(): ArrayList<EnemyShip> {
        return ArrayList(mEnemies)
    }

    fun destroyEnemies(enemies: List<StardroidShape?>) {
        mEnemies.removeAll(enemies)
    }

    fun addNewEnemySet(dt: Float) {
        createEnemy(dt)
    }

    private fun createEnemy(dt: Float) {
        mElapsedTime += dt
        if (mElapsedTime < mMillisecondsBetweenEnemyCreation) {
            return
        }

        mElapsedTime = 0f
        val ship = EnemyShip(mBounds[2], (Math.random() * Math.abs(mBounds[1] - mBounds[3]) - 1).toFloat())
        ship.moveToPosition(mBounds[0] * 2, ship.positionY)
        ship.engineSpeed = 5f

        mEnemies.add(ship)
    }

}
