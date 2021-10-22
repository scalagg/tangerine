package gg.scala.tangerine.queue.impl

import gg.scala.melon.MelonSpigotPlugin
import gg.scala.melon.commons.queue.impl.ParentQueue
import gg.scala.tangerine.queue.QueuePlatform
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/22/2021
 */
class MelonQueuePlatform : QueuePlatform
{
    override fun isInQueue(player: Player): Boolean
    {
        return getParent(player) != null
    }

    override fun getQueueName(player: Player): String
    {
        return getParent(player)!!.fancyName
    }

    override fun getQueuePosition(player: Player): Int
    {
        val cachedQueuePlayer = MelonSpigotPlugin.getInstance()
            .playerHandler.getByPlayer(player)

        return getParent(player)!!.getChildQueue(cachedQueuePlayer).orElse(null).getPosition(cachedQueuePlayer)
    }

    override fun getQueuePlayers(player: Player): Int
    {
        val cachedQueuePlayer = MelonSpigotPlugin.getInstance()
            .playerHandler.getByPlayer(player)

        return getParent(player)!!.getChildQueue(cachedQueuePlayer).orElse(null).allQueued
    }

    override fun getQueueLane(player: Player): String
    {
        val cachedQueuePlayer = MelonSpigotPlugin.getInstance()
            .playerHandler.getByPlayer(player)

        return getParent(player)!!.getChildQueue(cachedQueuePlayer).orElse(null).fancyName
    }

    private fun getParent(player: Player): ParentQueue?
    {
        val cachedQueuePlayer = MelonSpigotPlugin.getInstance()
            .playerHandler.getByPlayer(player)

        if (cachedQueuePlayer != null)
        {
            val parentQueueList = MelonSpigotPlugin.getInstance()
                .queueHandler.fetchPlayerQueuedIn(cachedQueuePlayer)

            if (parentQueueList.size > 0)
            {
                return parentQueueList[0]
            }
        }

        return null
    }
}
