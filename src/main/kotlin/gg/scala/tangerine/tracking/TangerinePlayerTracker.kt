package gg.scala.tangerine.tracking

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

    override fun run()
    {
        globalPlayers = Lemon.instance
            .network.overallPlayerCount
    }
}
