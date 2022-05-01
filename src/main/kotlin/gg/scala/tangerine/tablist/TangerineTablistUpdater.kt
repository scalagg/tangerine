package gg.scala.tangerine.tablist

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.placeholder.PlaceholderHandler
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import io.github.nosequel.tab.shared.entry.TabElement
import me.lucko.helper.Events
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 1/5/2022
 */
@Service
object TangerineTablistUpdater
{
    @JvmStatic
    val cached = ConcurrentHashMap<UUID, TabElement>()

    private val executor = Executors
        .newSingleThreadScheduledExecutor()

    @Configure
    fun configure()
    {
        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                cached.remove(it.player.uniqueId)
            }

        executor.scheduleAtFixedRate({
            for (player in Bukkit.getOnlinePlayers())
            {
                val element = cached[player.uniqueId]
                    ?: TabElement()
                element.entries.clear()

                val settings = TangerineSpigotPlugin.instance.settings
                val placeholder = PlaceholderHandler.findProvider()

                settings.tabLeft.forEachIndexed { index, string ->
                    element.add(0, index, placeholder?.handle(player, string)
                        ?.replace("{global}", TangerinePlayerTracker.globalPlayers.toString()) ?: string)
                }

                settings.tabMiddle.forEachIndexed { index, string ->
                    element.add(1, index, placeholder?.handle(player, string)
                        ?.replace("{global}", TangerinePlayerTracker.globalPlayers.toString()) ?: string)
                }

                settings.tabRight.forEachIndexed { index, string ->
                    element.add(2, index, placeholder?.handle(player, string)
                        ?.replace("{global}", TangerinePlayerTracker.globalPlayers.toString()) ?: string)
                }

                cached[player.uniqueId] = element
            }
        }, 0L, 500L, TimeUnit.MILLISECONDS)
    }
}
