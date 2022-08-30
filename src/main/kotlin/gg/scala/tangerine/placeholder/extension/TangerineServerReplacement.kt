package gg.scala.tangerine.placeholder.extension

import gg.scala.commons.agnostic.sync.server.ServerContainer
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.commons.replacement.Replacement
import gg.scala.commons.replacement.ReplacementBuilder
import gg.scala.commons.replacement.annotation.ReplacementAutoRegister
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
@ReplacementAutoRegister
object TangerineServerReplacement : Replacement
{
    fun server(id: String) = ServerContainer
        .allServers<GameServer>()
        .find {
            it.id.equals(id, true)
        }

    override val replacements =
        ReplacementBuilder
            .newBuilder()
            .process("global") { _, _ ->
                TangerinePlayerTracker.globalPlayers.toString()
            }
            .process("status") { _, id ->
                val server = this.server(id)
                    ?: return@process "${CC.RED}Server offline"

                if (server.getWhitelisted() == true)
                {
                    "${CC.YELLOW}Server whitelisted"
                } else
                {
                    "${CC.GREEN}Server online"
                }
            }
            .process("status-stripped") { _, id ->
                val server = this.server(id)
                    ?: return@process "${CC.RED}Offline"

                if (server.getWhitelisted() == true)
                {
                    "${CC.YELLOW}Whitelisted"
                } else
                {
                    "${CC.GREEN}Online"
                }
            }
            .process("join-status") { _, id ->
                val server = this.server(id)
                    ?: return@process "${CC.RED}Currently cannot join."

                if (server.getWhitelisted() == true)
                {
                    "${CC.YELLOW}Currently whitelisted!"
                } else
                {
                    "${CC.GREEN}Click to connect!"
                }
            }
            .process("scoreboard-display") { _, id ->
                val server = this.server(id)
                    ?: return@process "${CC.RED}Offline"

                if (server.getWhitelisted() == true)
                {
                    "${CC.YELLOW}Whitelisted"
                } else
                {
                    "${server.getPlayersCount()}/${server.getMaxPlayers()}"
                }
            }
            .process("players2") { _, id ->
                val server = this.server(id)
                    ?: return@process "0"

                "${server.getPlayersCount()}/${server.getMaxPlayers()}"
            }
            .process("players") { _, id ->
                val server = this.server(id)
                    ?: return@process "0"

                server.getPlayersCount().toString()
            }
            .process("max-players") { _, id ->
                val server = this.server(id)
                    ?: return@process "0"

                server.getMaxPlayers().toString()
            }
            .process("queued") { _, id ->
                TangerineSpigotPlugin.instance
                    .platform.queued(id).toString()
            }

    override fun id() = "tangerine"
}
