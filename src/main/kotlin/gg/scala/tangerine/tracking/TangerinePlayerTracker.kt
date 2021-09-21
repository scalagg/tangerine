package gg.scala.tangerine.tracking

import gg.scala.banana.BananaBuilder
import gg.scala.banana.credentials.BananaCredentials
import gg.scala.banana.options.BananaOptions

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
object TangerinePlayerTracker : Runnable {

    var globalPlayers = 0

    private val banana = BananaBuilder()
        .options(
            BananaOptions(
                channel = "tangerine",
                async = true
            )
        )
        .credentials(
            BananaCredentials()
        )
        .build()

    override fun run() {
        banana.useResource {
            val global = it.hget("cocoa:players", "global")
            globalPlayers = global?.toInt() ?: 0
        }
    }
}
