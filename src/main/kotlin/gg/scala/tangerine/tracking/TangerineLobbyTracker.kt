package gg.scala.tangerine.tracking

import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.annotations.runnables.Repeating
import me.lucko.helper.promise.ThreadContext
import java.util.*

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
@Repeating(20L, context = ThreadContext.ASYNC)
object TangerineLobbyTracker : Runnable
{
    var lobbies = mapOf<Int, GameServer>()

    override fun run()
    {
        val servers = ServerContainer.getServersInGroupCasted<GameServer>("hub")

        lobbies = servers
            .associateWith {
                Scanner(it.id)
                    .useDelimiter("\\D+")
            }
            .filterValues {
                it.hasNextInt()
            }
            .entries
            .associate {
                it.value.nextInt() to it.key
            }
    }
}
