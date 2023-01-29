package gg.scala.tangerine.tracking

import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.annotations.runnables.Repeating
import me.lucko.helper.promise.ThreadContext

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
@Repeating(2L, context = ThreadContext.ASYNC)
object TangerinePlayerTracker : Runnable
{
    var globalPlayers = 0

    override fun run()
    {
        globalPlayers = ServerContainer
            .allServers<GameServer>()
            .sumOf {
                it.getPlayersCount() ?: 0
            }
    }
}
