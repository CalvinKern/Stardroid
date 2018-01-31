package com.seakernel.stardroid

import com.seakernel.stardroid.model.EnemyController
import org.junit.Test
import kotlin.test.assertNotNull

/**
 * Created by Calvin on 1/30/18.
 * Copyright © 2018 SeaKernel. All rights reserved.
 */
class EnemyControllerTest {

    @Test
    fun getEnemiesTestNotNull() {
        val controller = EnemyController()
        assertNotNull(controller.getEnemies())
    }
}