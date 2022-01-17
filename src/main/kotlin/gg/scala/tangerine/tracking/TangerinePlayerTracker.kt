package gg.scala.tangerine.tracking

import gg.scala.lemon.Lemon

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
object TangerinePlayerTracker : Runnable
{
    var globalPlayers = 0

    override fun run()
    {
        globalPlayers = Lemon.instance
            .network.overallPlayerCount
    }
}
