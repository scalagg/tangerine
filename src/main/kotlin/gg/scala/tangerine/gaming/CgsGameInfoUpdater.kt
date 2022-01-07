package gg.scala.tangerine.gaming

import gg.scala.lemon.handler.ServerHandler
import gg.scala.lemon.server.ServerInstance
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 10/26/2021
 */
object CgsGameInfoUpdater : Thread("AGF - Info Thread")
{
    val gameInfo = mutableMapOf<CgsGameType, MutableList<ServerInstance>>()

    @JvmStatic
    val FIVE_SECONDS = TimeUnit.SECONDS.toMillis(5L)

    override fun run()
    {
        while (true)
        {
            try
            {
                CgsGameType.ALL.forEach { agfGameType ->
                    ServerHandler.fetchOnlineServerInstancesByGroup(agfGameType.group).thenAccept {
                        gameInfo[agfGameType] = it.values.toMutableList()
                    }

                    gameInfo[agfGameType]?.toList()?.forEach {
                        if (it.lastHeartbeat + FIVE_SECONDS < System.currentTimeMillis())
                        {
                            gameInfo[agfGameType]?.remove(it)
                        }
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
