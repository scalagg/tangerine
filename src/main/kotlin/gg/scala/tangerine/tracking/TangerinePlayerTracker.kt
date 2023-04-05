package gg.scala.tangerine.tracking

import gg.scala.commons.annotations.runnables.Repeating
import me.lucko.helper.promise.ThreadContext
import net.evilblock.cubed.ScalaCommonsSpigot

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
@Repeating(20L, context = ThreadContext.ASYNC)
object TangerinePlayerTracker : Runnable
{
    var globalPlayers = 0

    override fun run()
    {
        globalPlayers = ScalaCommonsSpigot
            .instance.kvConnection
            .sync().get(
                "global-player-count"
            )
            ?.toIntOrNull() ?: 0
    }
}
