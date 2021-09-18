package com.solexgames.tangerine.tracking

import com.solexgames.lemon.Lemon
import com.solexgames.redis.JedisBuilder
import com.solexgames.redis.JedisManager

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
object TangerinePlayerTracker : Runnable {

    var globalPlayers = 0

    private val tangerineRedisManager: JedisManager = JedisBuilder()
        .withChannel("tangerine")
        .withSettings(Lemon.instance.jedisSettings)
        .build()

    override fun run() {
        tangerineRedisManager.runCommand {
            val global = it.hget("cocoa:players", "global")
            globalPlayers = global?.toInt() ?: 0
        }
    }
}
