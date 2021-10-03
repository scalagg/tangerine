package gg.scala.tangerine.tracking

import gg.scala.banana.BananaBuilder
import gg.scala.banana.options.BananaOptions
import gg.scala.lemon.Lemon
import java.util.concurrent.ForkJoinPool

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
                async = true,
            )
        )
        .credentials(
            Lemon.instance.credentials
        )
        .build()

    override fun run() {
        ForkJoinPool.commonPool().execute {
            banana.useResource {
                val global = it.hget("cocoa:players", "global")
                globalPlayers = global?.toInt() ?: 0

                println("$globalPlayers $global")
            }
        }
    }
}
