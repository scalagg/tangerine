package gg.scala.tangerine.queue.impl

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan
import gg.scala.queue.spigot.command.JoinQueueCommand
import gg.scala.queue.spigot.stream.SpigotRedisService
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.queue.QueuePlatform
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 10/22/2021
 */
@Service
@IgnoreAutoScan
@SoftDependency("ScQueue")
object ScalaQueuePlatform : QueuePlatform
{
    @Inject
    lateinit var plugin: TangerineSpigotPlugin

    @Configure
    fun configure()
    {
        this.plugin.platform = this
    }

    override fun isInQueue(player: Player): Boolean
    {
        return getQueue(player) != null
    }

    override fun queued(server: String): Int
    {
        return getQueue(server)?.value?.total() ?: 0
    }

    override fun getQueueName(player: Player): String
    {
        return getQueue(player)?.displayName ?: "N/A"
    }

    override fun getQueuePosition(player: Player): Int
    {
        return getQueue(player)?.position(player.uniqueId) ?: 0
    }

    override fun getQueuePlayers(player: Player): Int
    {
        return getQueue(player)?.players?.size ?: 0
    }

    override fun joinQueue(player: Player, server: String)
    {
        val wrapper = SpigotRedisService.queues[server]

        if (wrapper == null)
        {
            player.sendMessage("${CC.RED}Sorry, we were unable to queue you for this server.")
            return
        }

        kotlin.runCatching {
            JoinQueueCommand.onJoinQueue(player, wrapper)
        }.onFailure {
            // possible security issue if we don't check
            if (it is ConditionFailedException)
            {
                player.sendMessage("${CC.RED}${it.message}")
            } else
            {
                player.sendMessage("${CC.RED}Something went terribly wrong while processing this task.")
            }
        }
    }

    private fun getQueue(player: Player) =
        SpigotRedisService.findQueuePlayerIsIn(player)

    private fun getQueue(server: String) =
        SpigotRedisService.queues.entries
            .firstOrNull { it.key == server }
}
