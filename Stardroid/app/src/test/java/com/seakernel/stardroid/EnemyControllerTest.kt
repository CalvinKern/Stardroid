package com.seakernel.stardroid

import com.seakernel.stardroid.model.EnemyController
import com.seakernel.stardroid.model.controller.StardroidShape
import com.seakernel.stardroid.model.controller.ship.EnemyShip
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Created by Calvin on 1/30/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */
class EnemyControllerTest {

    @Test
    fun getEnemiesTestNotNull() {
        val controller = EnemyController()
        assertNotNull(controller.getShapes())
    }

    @Test
    fun destroyEnemiesTestWillReducesCreationTime() {
        val controller = EnemyController()
        controller.resetState()
        val time = controller.getTimeBetweenEnemyCreation()

        val list = ArrayList<StardroidShape>()
        list.add(EnemyShip(0f, 0f))

        controller.destroyShapes(list)

        assertEquals(time - list.size, controller.getTimeBetweenEnemyCreation())
    }

    @Test
    fun destroyEnemiesTestWillNotReduceCreationTimeMillisecondsPastMin() {
        val controller = EnemyController()

        controller.setTimeBetweenEnemyCreation(controller.MIN_MILLISECONDS_BETWEEN_ENEMY_CREATION)

        val list = ArrayList<StardroidShape>()
        list.add(EnemyShip(0f, 0f))

        controller.destroyShapes(list)

        assertEquals(controller.MIN_MILLISECONDS_BETWEEN_ENEMY_CREATION, controller.getTimeBetweenEnemyCreation())
    }
}