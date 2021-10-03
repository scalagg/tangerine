package gg.scala.tangerine.placeholder.extension

import com.solexgames.daddyshark.commons.model.ServerInstance
import gg.scala.lemon.Lemon
import gg.scala.tangerine.TangerineSpigotPlugin
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

    private val cachedServers = mutableMapOf<String, ServerInstance>()
    private val serverRunnables = mutableListOf<Runnable>()

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
        val networkServer = fetchOrGetServer(arguments[1].replace("|", "-"))

        when (arguments[0].lowercase(Locale.getDefault())) {
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
            "sbdisplay" -> return if (networkServer == null) {
                "${CC.RED}Offline"
            } else if (networkServer.whitelisted){
                "${CC.YELLOW}Whitelisted"
            } else {
                "${CC.WHITE}${networkServer.onlinePlayers}/${networkServer.maxPlayers}"
            }
            "players" -> return if (networkServer == null) {
                "0"
            } else {
                "${networkServer.onlinePlayers}"
            }
            "max" -> return if (networkServer == null) {
                "0"
            } else {
                "${networkServer.maxPlayers}"
            }
        }

        return "${CC.GOLD}Unavailable."
    }

    private fun fetchOrGetServer(id: String): ServerInstance? {
        if (cachedServers.containsKey(id)) {
            return cachedServers[id]!!
        }

        val runnable = UpdateRunnable(id)

        Schedulers.async()
            .runRepeating(runnable, 0L, 20L)

        serverRunnables.add(runnable)

        return null
    }

    private class UpdateRunnable(
        private val instance: String
    ) : Runnable {

        override fun run() {
            Lemon.instance.handler.fetchServerInstanceById(instance)?.thenAccept {
                run {
                    cachedServers[instance] = it
                }
            }
        }
    }
}
