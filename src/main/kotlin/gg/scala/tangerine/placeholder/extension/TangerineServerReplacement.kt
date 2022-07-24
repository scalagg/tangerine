package gg.scala.tangerine.placeholder.extension

import gg.scala.commons.replacement.Replacement
import gg.scala.commons.replacement.ReplacementBuilder
import gg.scala.commons.replacement.annotation.ReplacementAutoRegister
import gg.scala.lemon.handler.ServerHandler
import gg.scala.lemon.server.ServerInstance
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
@ReplacementAutoRegister
object TangerineServerReplacement : Replacement
{
    private val cachedServers =
        mutableMapOf<String, ServerInstance?>()

    private val serverRunnables =
        mutableListOf<UpdateRunnable>()

    override val replacements =
        ReplacementBuilder
            .newBuilder()
            .process("global") { _, _ ->
                TangerinePlayerTracker.globalPlayers.toString()
            }

            .process("status") { _, id ->
                val server = this.server(id)
                    ?: return@process "${CC.RED}Server offline"

                if (server.whitelisted)
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

                if (server.whitelisted)
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

                if (server.whitelisted)
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

                if (server.whitelisted)
                {
                    "${CC.YELLOW}Whitelisted"
                } else
                {
                    "${server.onlinePlayers}/${server.maxPlayers}"
                }
            }
            .process("players2") { _, id ->
                val server = this.server(id)
                    ?: return@process "0"

                "${server.onlinePlayers}/${server.maxPlayers}"
            }
            .process("players") { _, id ->
                val server = this.server(id)
                    ?: return@process "0"

                server.onlinePlayers.toString()
            }
            .process("max-players") { _, id ->
                val server = this.server(id)
                    ?: return@process "0"

                server.maxPlayers.toString()
            }
            .process("queued") { _, id ->
                TangerineSpigotPlugin.instance
                    .platform.queued(id).toString()
            }

    override fun id() = "tangerine"

    private fun server(id: String): ServerInstance?
    {
        if (cachedServers.containsKey(id))
        {
            return cachedServers[id]
        }

        val foundUpdater = serverRunnables
            .firstOrNull {
                it.instance.equals(id, true)
            }

        if (foundUpdater != null)
        {
            // there's an active updater for this instance,
            // it just hasn't fetched the server yet.
            return null
        }

        val runnable = UpdateRunnable(id)
            .apply {
                serverRunnables.add(this)
            }

        Schedulers.async().runRepeating(
            runnable, 0L, 10L
        )

        return null
    }

    class UpdateRunnable(
        val instance: String
    ) : Runnable
    {
        override fun run()
        {
            ServerHandler
                .fetchServerInstanceById(instance)
                .thenAccept {
                    cachedServers[instance] = it
                }
                .join()
        }
    }
}
