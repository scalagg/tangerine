package gg.scala.tangerine.items2

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.tangerine.TangerineSpigotPlugin
import me.lucko.helper.Events
import me.lucko.helper.terminable.composite.CompositeTerminable
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerInteractEvent

/**
 * @author GrowlyX
 * @since 7/12/2022
 */
@Service
object TangerineItems2Service
{
    @Inject
    lateinit var plugin: TangerineSpigotPlugin

    private var terminable = CompositeTerminable.create()

    @Configure
    fun configure()
    {
        val config = plugin
            .config<Items2Config>()

        Events.subscribe(PlayerInteractEvent::class.java)
            .filter {
                it.hasItem() &&
                it.action.name.contains("RIGHT")
            }
            .handler {
                val item = it.item

                val matching = config.items
                    .first { model ->
                        model.item.isSimilar(item)
                    }

                matching.action.lambda(
                    it.player, matching.meta
                )
            }
            .bindWith(terminable)
    }

    @Close
    fun close()
    {
        terminable.closeAndReportException()
        terminable = CompositeTerminable.create()
    }

    fun reload()
    {
        close()
        configure()

        Bukkit.getOnlinePlayers().forEach {
            if (!it.hasMetadata("builder"))
            {
                it.inventory.clear()

                plugin
                    .config<Items2Config>()
                    .items
                    .forEach { model ->
                        it.inventory.setItem(model.position, model.item)
                    }
            }
        }
    }
}
