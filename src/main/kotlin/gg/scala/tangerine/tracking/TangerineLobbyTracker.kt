package gg.scala.tangerine.tracking

import com.solexgames.daddyshark.commons.model.ServerInstance
import gg.scala.lemon.Lemon
import java.util.*

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
object TangerineLobbyTracker : Runnable {

    val lobbies = mutableMapOf<Int, ServerInstance>()

    override fun run() {
        Lemon.instance.handler.fetchOnlineServerInstancesByGroup("hub")?.thenAccept { instances ->
            instances
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
