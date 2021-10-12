package gg.scala.tangerine.tracking

import gg.scala.lemon.handler.ServerHandler
import gg.scala.lemon.server.ServerInstance
import java.util.*

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
object TangerineLobbyTracker : Runnable {

    val lobbies = mutableMapOf<Int, ServerInstance>()

    override fun run() {
        ServerHandler.fetchOnlineServerInstancesByGroup("hub").thenAccept { instances ->
            instances.values
                .map {
                    Pair(
                        Scanner(it.serverId)
                            .useDelimiter("\\D+")
                            .nextInt(),
                        it
                    )
                }
                .sortedBy { it.first }
                .forEach {
                    lobbies[it.first] = it.second
                }
        }
    }
}
