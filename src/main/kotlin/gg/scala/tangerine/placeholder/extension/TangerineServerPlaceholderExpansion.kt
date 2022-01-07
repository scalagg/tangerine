package gg.scala.tangerine.placeholder.extension

import gg.scala.lemon.handler.ServerHandler
import gg.scala.lemon.server.ServerInstance
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.lucko.helper.Schedulers
import net.evilblock.cubed.util.CC
import org.bukkit.OfflinePlayer
import java.util.*

/**
 * @author GrowlyX
 * @since 9/24/2021
 */
object TangerineServerPlaceholderExpansion : PlaceholderExpansion() {

    val cachedServers = mutableMapOf<String, ServerInstance?>()
    val serverRunnables = mutableListOf<UpdateRunnable>()

    override fun getIdentifier(): String {
        return "tangerine"
    }

    override fun getAuthor(): String {
        return "Scala"
    }

    override fun getVersion(): String {
        return TangerineSpigotPlugin.instance.description.version
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        val arguments = params.split("-")
        val server = arguments[1].replace("|", "-")
        val networkServer = fetchOrGetServer(server)

        when (arguments[0].lowercase()) {
            "status" -> return if (networkServer == null) {
                "${CC.RED}Server offline"
            } else {
                return if (networkServer.whitelisted) {
                    "${CC.YELLOW}Server whitelisted"
                } else {
                    "${CC.GREEN}Server online"
                }
            }
            "joinable" -> return if (networkServer == null) {
                "${CC.RED}Currently cannot join."
            } else {
                return if (networkServer.whitelisted) {
                    "${CC.YELLOW}Currently whitelisted!"
                } else {
                    "${CC.GREEN}Click to connect!"
                }
            }
            "globalplayers" -> TangerinePlayerTracker.globalPlayers
            "sbdisplay" -> return if (networkServer == null) {
                "${CC.RED}Offline"
            } else if (networkServer.whitelisted){
                "${CC.RED}Whitelisted"
            } else {
                "${CC.WHITE}${networkServer.onlinePlayers}/${networkServer.maxPlayers}"
            }
            "players" -> return if (networkServer == null) {
                "0"
            } else {
                "${networkServer.onlinePlayers}"
            }
            "complete" -> return if (networkServer == null) {
                "0/0 ${CC.GRAY}(${
                    TangerineSpigotPlugin.instance.platform.queued(server)
                } queued)"
            } else {
                "${networkServer.onlinePlayers}/${networkServer.maxPlayers} ${CC.GRAY}(${
                    TangerineSpigotPlugin.instance.platform.queued(server)
                } queued)"
            }
            "max" -> return if (networkServer == null) {
                "0"
            } else {
                "${networkServer.maxPlayers}"
            }
        }

        return "${CC.GOLD}Loading..."
    }

    private fun fetchOrGetServer(id: String): ServerInstance? {
        if (cachedServers.containsKey(id)) {
            return cachedServers[id]
        }

        val foundUpdater = serverRunnables.firstOrNull {
            it.instance == id
        }

        if (foundUpdater != null) {
            // there's an active updater for this instance,
            // it just hasn't fetched the server yet.
            return null
        }

        val runnable = UpdateRunnable(id)

        Schedulers.async().runRepeating(
            runnable, 0L, 10L
        )

        serverRunnables.add(runnable)

        return null
    }

    class UpdateRunnable(
        val instance: String
    ) : Runnable {

        override fun run() {
            ServerHandler.fetchServerInstanceById(
                instance
            ).thenAccept {
                cachedServers[instance] = it
            }
        }
    }
}
