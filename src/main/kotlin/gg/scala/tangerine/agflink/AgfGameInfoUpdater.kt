package gg.scala.tangerine.agflink

import gg.scala.lemon.handler.ServerHandler
import gg.scala.lemon.server.ServerInstance

/**
 * @author GrowlyX
 * @since 10/26/2021
 */
object AgfGameInfoUpdater : Thread("AGF - Info Thread")
{
    val gameInfo = mutableMapOf<AgfGameType, List<ServerInstance>>()

    override fun run()
    {
        while (true)
        {
            try
            {
                AgfGameType.ALL.forEach { agfGameType ->
                    ServerHandler.fetchOnlineServerInstancesByGroup(agfGameType.group).thenAccept {
                        gameInfo[agfGameType] = it.values.toList()
                    }
                }
            } catch (exception: Exception)
            {
                exception.printStackTrace()
            }

            try
            {
                sleep(1000L)
            } catch (ignored: Exception)
            {
                println("Failed to continue AGF Info Thread.")
            }
        }
    }
}
