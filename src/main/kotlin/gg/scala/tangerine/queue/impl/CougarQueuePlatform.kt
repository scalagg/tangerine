package gg.scala.tangerine.queue.impl

import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.tangerine.queue.QueuePlatform
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/22/2021
 */
class CougarQueuePlatform : QueuePlatform
{
    override fun isInQueue(player: Player): Boolean
    {
        return getQueue(player) != null
    }

    override fun queued(server: String): Int
    {
        return getQueue(server)?.sizeOfQueue() ?: 0
    }

    override fun getQueueName(player: Player): String
    {
        return getQueue(player)?.fancyDisplayName ?: "N/A"
    }

    override fun getQueuePosition(player: Player): Int
    {
        return getQueue(player)?.getPosition(player.uniqueId) ?: 0
    }

    override fun getQueuePlayers(player: Player): Int
    {
        return getQueue(player)?.sizeOfQueue() ?: 0
    }

    private fun getQueue(player: Player): CougarQueueWrapper?
    {
        return ScCougarPlatform.INSTANCE.queueLayer
            .localCache.values.firstOrNull {
                it.internalQueue.contains(player.uniqueId)
            }
    }

    private fun getQueue(server: String): CougarQueueWrapper?
    {
        return ScCougarPlatform.INSTANCE.queueLayer
            .localCache.values.firstOrNull {
                it.serverId.equals(server, true)
            }
    }
}
