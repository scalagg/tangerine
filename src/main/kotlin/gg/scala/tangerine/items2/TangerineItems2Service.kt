package gg.scala.tangerine.items2

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.tangerine.TangerineSpigotPlugin
import me.lucko.helper.Events
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue

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
        Events.subscribe(PlayerInteractEvent::class.java)
            .filter {
                it.hasItem() &&
                it.action.name.contains("RIGHT")
            }
            .handler {
                val config = plugin
                    .config<Items2Config>()

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

        plugin.config<Items2Config>()
            .items
            .filter {
                it.action == Items2Action.PlayerVisibility
            }
            .forEach { model ->
                val stack = model.item

                val enabledStack = ItemBuilder(stack.clone())
                    .name(model.meta.first())
                    .data(10).build()

                val disabledStack = ItemBuilder(stack.clone())
                    .name(model.meta.last())
                    .data(8).build()

                Events.subscribe(PlayerInteractEvent::class.java)
                    .filter {
                        it.hasItem() &&
                        it.action.name.contains("RIGHT") &&
                        it.item.isSimilar(stack)
                    }
                    .handler {
                        val hidingNow = it.player.hasMetadata("hiding-players")

                        if (hidingNow)
                        {
                            it.player.removeMetadata("hiding-players", TangerineSpigotPlugin.instance)
                        } else
                        {
                            it.player.setMetadata("hiding-players", FixedMetadataValue(plugin, true))
                        }

                        it.player.inventory.setItem(
                            model.position,
                            if (hidingNow)
                            {
                                enabledStack
                            } else
                            {
                                disabledStack
                            }
                        )
                        it.player.updateInventory()

                        VisibilityHandler.updateAllTo(it.player)
                    }
                    .bindWith(terminable)
            }
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
