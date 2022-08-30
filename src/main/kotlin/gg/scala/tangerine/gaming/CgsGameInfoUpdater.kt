package gg.scala.tangerine.gaming

import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 10/26/2021
 */
object CgsGameInfoUpdater : Thread("AGF - Info Thread")
{
    val gameInfo = ConcurrentHashMap<CgsGameType, MutableList<GameServer>>()

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
                            ServerContainer
                                .allServers<GameServer>()
                                .filter {
                                    "${agfGameType.group}-$region" in it.groups
                                }
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
