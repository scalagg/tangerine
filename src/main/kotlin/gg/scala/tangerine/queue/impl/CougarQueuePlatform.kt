package gg.scala.tangerine.queue.impl

import co.aikar.commands.ConditionFailedException
import gg.scala.cougar.common.ScCougarPlatform
import gg.scala.cougar.common.queue.impl.wrapper.CougarQueueWrapper
import gg.scala.cougar.spigot.ScalaCougarSpigotEngine
import gg.scala.cougar.spigot.command.impl.JoinQueueCommand
import gg.scala.lemon.handler.ServerHandler
import gg.scala.lemon.server.ServerInstance
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.spigot.ScalaDataStoreSpigot
import gg.scala.store.spigot.ScalaDataStoreSpigotImpl
import gg.scala.store.storage.impl.CachedDataStoreStorageLayer
import gg.scala.store.storage.type.DataStoreStorageType
import gg.scala.tangerine.queue.QueuePlatform
import net.evilblock.cubed.util.CC
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

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

    override fun joinQueue(player: Player, server: String)
    {
        val wrapper = ScalaCougarSpigotEngine.queueLayer
            .localCache().values.firstOrNull { wrapper ->
                wrapper.serverId.equals(server, true)
            }

        if (wrapper == null)
        {
            player.sendMessage("${CC.RED}Sorry, we were unable to queue you for this server.")
            return
        }

        JoinQueueCommand.onJoinQueue(player, wrapper)
    }

    private fun getQueue(player: Player): CougarQueueWrapper?
    {
        return ScalaCougarSpigotEngine.queueLayer
            .localCache().values.firstOrNull {
                it.internalQueue.contains(player.uniqueId)
            }
    }

    private fun getQueue(server: String): CougarQueueWrapper?
    {
        return ScalaCougarSpigotEngine.queueLayer
            .localCache().values.firstOrNull {
                it.serverId.equals(server, true)
            }
    }
}
