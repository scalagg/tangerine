package gg.scala.tangerine.tracking

import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.annotations.runnables.Repeating
import gg.scala.lemon.Lemon
import me.lucko.helper.promise.ThreadContext

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
@Repeating(2L, context = ThreadContext.ASYNC)
object TangerinePlayerTracker : Runnable
{
    var globalPlayers = 0

    private val connection = Lemon.instance
        .aware.internal().connect()!!

    override fun run()
    {
        val count = connection.sync()
            .get("global-player-count")

        if (count != null)
        {
            this.globalPlayers = count.toInt()
            return
        }

        globalPlayers = ServerContainer
            .allServers<GameServer>()
            .sumOf {
                it.getPlayersCount() ?: 0
            }
    }
}
