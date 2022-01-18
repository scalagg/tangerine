package gg.scala.tangerine.queue

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/22/2021
 */
interface QueuePlatform
{
    fun isInQueue(player: Player): Boolean
    fun queued(server: String): Int

    fun getQueueName(player: Player): String
    fun getQueuePosition(player: Player): Int
    fun getQueuePlayers(player: Player): Int

    fun joinQueue(player: Player, server: String)
}
