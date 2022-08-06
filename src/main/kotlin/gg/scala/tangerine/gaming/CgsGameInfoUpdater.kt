package gg.scala.tangerine.gaming

import gg.scala.lemon.handler.ServerHandler
import gg.scala.lemon.server.ServerInstance
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 10/26/2021
 */
object CgsGameInfoUpdater : Thread("AGF - Info Thread")
{
    val gameInfo = ConcurrentHashMap<CgsGameType, MutableList<ServerInstance>>()

    @JvmStatic
    val FIVE_SECONDS = TimeUnit.SECONDS.toMillis(5L)

    override fun run()
    {
        while (true)
        {
            try
            {
                CgsGameType.ALL.forEach { agfGameType ->
                    this.gameInfo[agfGameType] = mutableListOf()

                    for (region in listOf("na", "as", "eu", "sa"))
                    {
                        this.gameInfo[agfGameType]?.addAll(
                            ServerHandler
                                .fetchOnlineServerInstancesByGroup(
                                    "${agfGameType.group}-$region"
                                )
                                .join().values
                        )
                    }
                }
            } catch (exception: Exception)
            {
                exception.printStackTrace()
            }

            try
            {
                sleep(250L)
            } catch (ignored: Exception)
            {
                println("Failed to continue AGF Info Thread.")
            }
        }
    }
}
