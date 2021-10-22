package gg.scala.tangerine.queue.impl

import gg.scala.tangerine.queue.QueuePlatform
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/22/2021
 */
object DefaultQueuePlatform : QueuePlatform
{
    override fun isInQueue(player: Player): Boolean
    {
        return false
    }

    override fun getQueueName(player: Player): String
    {
        return ""
    }

    override fun getQueueLane(player: Player): String
    {
        return ""
    }

    override fun getQueuePosition(player: Player): Int
    {
        return 0
    }

    override fun getQueuePlayers(player: Player): Int
    {
        return 0
    }
}
