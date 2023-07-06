package gg.scala.tangerine.items2

import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.settings.TangerineSettings
import me.lucko.helper.Events
import me.lucko.helper.cooldown.Cooldown
import me.lucko.helper.cooldown.CooldownMap
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.UUID
import java.util.concurrent.TimeUnit

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
            .filter {
                it.clickedBlock == null
            }
            .handler {
                val config = plugin
                    .config<Items2Config>()

                val item = it.item

                val matching = config.items
                    .firstOrNull { model ->
                        model.item.isSimilar(item)
                    }
                    ?: return@handler

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
            .forEach(::configureVisibility)
    }

    @Close
    fun close()
    {
        terminable.closeAndReportException()
        terminable = CompositeTerminable.create()
    }

    private val cache = mutableMapOf<UUID, Boolean>()

    private fun configureVisibility(model: Items2Model)
    {
        val config = plugin
            .config<TangerineSettings>()

        val coolDowns = CooldownMap
            .create<UUID>(
                Cooldown.of(
                    config.playerVisibilityCooldown.toLong(),
                    TimeUnit.valueOf(config.playerVisibilityCooldownUnit)
                )
            )

        val stack = model.item

        val enabledStack = ItemBuilder(stack.clone())
            .name(model.meta[0])
            .data(10).build()

        val disabledStack = ItemBuilder(stack.clone())
            .name(model.meta[1])
            .data(8).build()

        Events.subscribe(PlayerJoinEvent::class.java)
            .handler {
                cache[it.player.uniqueId] = false
            }
            .bindWith(terminable)

        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                cache.remove(it.player.uniqueId)
            }
            .bindWith(terminable)

        Events.subscribe(PlayerInteractEvent::class.java)
            .filter {
                it.hasItem() && it.clickedBlock == null && (it.item.isSimilar(enabledStack) || it.item.isSimilar(disabledStack) || it.item.isSimilar(stack) || (stack.itemMeta.displayName.contains("Profile") && it.item.itemMeta.displayName.contains("Profile"))) && it.action == Action.RIGHT_CLICK_AIR
            }
            .handler {
                val hiding =
                    cache[it.player.uniqueId] ?: true

                if (!coolDowns.test(it.player.uniqueId))
                {
                    val remaining = coolDowns
                        .remainingTime(
                            it.player.uniqueId,
                            TimeUnit.MILLISECONDS
                        ) / 1000.0F

                    it.player.sendMessage(
                        config.playerVisibilityCooldownMessage
                            .format("%.2f".format(remaining))
                    )
                    return@handler
                }

                cache[it.player.uniqueId] = !hiding

                if (hiding)
                {
                    model.meta.getOrNull(2)
                        ?.apply {
                            it.player.sendMessage(this)
                        }
                } else
                {
                    model.meta.getOrNull(3)
                        ?.apply {
                            it.player.sendMessage(this)
                        }
                }

                kotlin.runCatching {
                    it.player.inventory.setItem(
                        model.position,
                        if (hiding)
                            enabledStack else disabledStack
                    )
                    it.player.updateInventory()

                    VisibilityHandler.updateAllTo(it.player)
                }
            }
            .bindWith(terminable)
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
