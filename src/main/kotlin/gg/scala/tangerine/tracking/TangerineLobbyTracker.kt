package gg.scala.tangerine.tracking

import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.annotations.runnables.Repeating
import me.lucko.helper.promise.ThreadContext
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
@Repeating(20L, context = ThreadContext.ASYNC)
object TangerineLobbyTracker : Runnable
{
    val lobbies = ConcurrentHashMap<Int, GameServer>()

    override fun run()
    {
        val servers = ServerContainer
            .allServers<GameServer>()
            .filter {
                "hub" in it.groups
            }

        this.lobbies.clear()

        servers
            .map {
                Scanner(it.id)
                    .useDelimiter("\\D+")
                    .nextInt() to it
            }
            .sortedBy { it.first }
            .forEach {
                lobbies[it.first] = it.second
            }
    }
}
