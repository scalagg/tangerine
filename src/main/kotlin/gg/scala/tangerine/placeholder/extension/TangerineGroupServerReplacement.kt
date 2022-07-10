package gg.scala.tangerine.placeholder.extension

import gg.scala.commons.replacement.Replacement
import gg.scala.commons.replacement.ReplacementBuilder
import gg.scala.commons.replacement.annotation.ReplacementAutoRegister
import gg.scala.lemon.handler.ServerHandler
import gg.scala.lemon.server.ServerInstance
import gg.scala.lemon.server.aggregate.ServerGroupAggregate
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
@ReplacementAutoRegister
object TangerineGroupServerReplacement : Replacement
{
    private val cachedAggregates = mutableMapOf<String, ServerGroupAggregate>()

    override val replacements =
        ReplacementBuilder
            .newBuilder()
            .process("scoreboard-display") { _, id ->
                val server = this.aggregate(id)

                when (
                    server.prominentServerStatus ?: ServerInstance.ServerStatus.OFFLINE
                )
                {
                    ServerInstance.ServerStatus.WHITELISTED -> "${CC.RED}Whitelisted"
                    ServerInstance.ServerStatus.OFFLINE -> "${CC.RED}Offline"
                    ServerInstance.ServerStatus.ONLINE -> "${server.totalPlayerCount ?: 0}"
                }
            }
            .process("scoreboard-display2") { _, id ->
                val server = this.aggregate(id)

                when (
                    server.prominentServerStatus ?: ServerInstance.ServerStatus.OFFLINE
                )
                {
                    ServerInstance.ServerStatus.WHITELISTED -> "${CC.RED}Whitelisted"
                    ServerInstance.ServerStatus.OFFLINE -> "0"
                    ServerInstance.ServerStatus.ONLINE -> "${server.totalPlayerCount ?: 0}"
                }
            }
            .process("players") { _, id ->
                "${this.aggregate(id) .totalPlayerCount ?: 0}"
            }
            .process("servers") { _, id ->
                "${this.aggregate(id).totalServerCount ?: 0}"
            }
            .process("max-players") { _, id ->
                "${this.aggregate(id).totalMaxPlayerCount ?: 0}"
            }

    override fun id() = "tangerineGroups"

    private fun aggregate(id: String): ServerGroupAggregate
    {
        if (cachedAggregates.containsKey(id))
        {
            return cachedAggregates[id]!!
        }

        val aggregate = ServerGroupAggregate(id)
            .apply {
                subscribeAggregateRefresh()
            }

        this.cachedAggregates[id] = aggregate

        return aggregate
    }
}
