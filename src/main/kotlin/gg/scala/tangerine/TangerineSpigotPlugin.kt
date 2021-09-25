package gg.scala.tangerine

import com.google.gson.LongSerializationPolicy
import gg.scala.lemon.Lemon
import gg.scala.lemon.adapt.LemonPlayerAdapter
import gg.scala.lemon.adapt.UUIDAdapter
import gg.scala.lemon.player.LemonPlayer
import gg.scala.tangerine.items.ConfigurableItemHandler
import gg.scala.tangerine.listener.WorldListener
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.configurable.ConfigurableMenuOutput
import gg.scala.tangerine.menu.LobbySelectorMenu
import gg.scala.tangerine.module.HubModule
import gg.scala.tangerine.scoreboard.TangerineScoreboardOverride
import gg.scala.tangerine.settings.TangerineSettings
import gg.scala.tangerine.tracking.TangerineLobbyTracker
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import gg.scala.tangerine.visibility.TangerineVisibilityAdapter
import gg.scala.tangerine.visibility.TangerineVisibilityOverride
import io.papermc.lib.PaperLib
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.plugin.ExtendedJavaPlugin
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.command.manager.CubedCommandManager
import net.evilblock.cubed.entity.EntitySerializer
import net.evilblock.cubed.entity.animation.EntityAnimation
import net.evilblock.cubed.menu.template.MenuTemplate
import net.evilblock.cubed.menu.template.MenuTemplateButton
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.cubed.serialize.BlockVectorAdapter
import net.evilblock.cubed.serialize.ItemStackAdapter
import net.evilblock.cubed.serialize.LocationAdapter
import net.evilblock.cubed.serialize.VectorAdapter
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.serializers.impl.AbstractTypeSerializer
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.BlockVector
import xyz.mkotb.configapi.ConfigFactory
import java.util.*

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
class TangerineSpigotPlugin : ExtendedJavaPlugin() {

    companion object {
        @JvmStatic
        lateinit var instance: TangerineSpigotPlugin

        fun reloadConfig() {
            instance.settings = instance.configFactory.fromFile(
                "settings", TangerineSettings::class.java
            )
        }
    }

    var itemTerminable = CompositeTerminable.create()

    lateinit var settings: TangerineSettings

    var hubModule: HubModule? = null

    val configFactory: ConfigFactory = ConfigFactory.newFactory(this)

    override fun enable() {
        instance = this

        settings = configFactory.fromFile(
            "settings", TangerineSettings::class.java
        )

        ScoreboardHandler.scoreboardOverride =
            hubModule?.scoreboardAdapter ?: TangerineScoreboardOverride(this.settings)

        if (settings.autoHidePlayers) {
            VisibilityHandler.registerAdapter("tangerine", TangerineVisibilityAdapter)
            VisibilityHandler.registerOverride("tangerine", TangerineVisibilityOverride)
        }

        Serializers.useGsonBuilderThenRebuild { builder ->
            builder.serializeNulls().setPrettyPrinting()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .registerTypeHierarchyAdapter(ItemStack::class.java, ItemStackAdapter())
                .registerTypeHierarchyAdapter(Location::class.java, LocationAdapter())
                .registerTypeHierarchyAdapter(Vector::class.java, VectorAdapter())
                .registerTypeHierarchyAdapter(BlockVector::class.java, BlockVectorAdapter())
                .registerTypeAdapter(Entity::class.java, EntitySerializer)
                .registerTypeAdapter(UUID::class.java, UUIDAdapter)
                .registerTypeAdapter(LemonPlayer::class.java, LemonPlayerAdapter)
                .registerTypeAdapter(EntityAnimation::class.java, AbstractTypeSerializer<EntityAnimation>())
                .registerTypeAdapter(MenuTemplate::class.java, AbstractTypeSerializer<MenuTemplate<*>>())
                .registerTypeAdapter(MenuTemplateButton::class.java, AbstractTypeSerializer<MenuTemplateButton>())
        }

        ConfigurableMenuHandler.initialLoad()
        ConfigurableItemHandler.initialLoad()

        Schedulers.async().runRepeating(
            TangerinePlayerTracker, 0L, 20L
        )
        Schedulers.async().runRepeating(
            TangerineLobbyTracker, 0L, 20L
        )

        this.registerListeners()
        this.registerCommands()
    }

    private fun registerCommands() {
        val commandManager = CubedCommandManager(
            plugin = this,
            primary = ChatColor.valueOf(Lemon.instance.lemonWebData.primary),
            secondary = ChatColor.valueOf(Lemon.instance.lemonWebData.secondary)
        )

        commandManager.commandContexts.registerContext(ConfigurableMenuOutput::class.java) {
            val firstArg = it.popFirstArg()

            val template = ConfigurableMenuHandler.templates[firstArg]
                ?: throw ConditionFailedException("No menu by the name ${CC.YELLOW}$firstArg${CC.RED} was found.")

            return@registerContext ConfigurableMenuOutput(template)
        }

        Lemon.instance.registerCompletionsAndContexts(commandManager)
        Lemon.instance.registerCommandsInPackage(commandManager, "gg.scala.tangerine.command")

        println("Loaded command manager")
    }

    private fun registerListeners() {
        Events.subscribe(PlayerJoinEvent::class.java).handler { event ->
            val player = event.player

            player.inventory.clear()
            player.inventory.armorContents = arrayOfNulls(4)

            player.health = 20.0
            player.foodLevel = 20
            player.gameMode = GameMode.ADVENTURE

            player.walkSpeed = 0.4f
            player.allowFlight = settings.allowDoubleJump

            if (settings.spawnLocation != null) {
                PaperLib.teleportAsync(player, settings.spawnLocation!!)
            }

            if (settings.joinMessage.isNotEmpty()) {
                settings.joinMessage.forEach { player.sendMessage(it) }
            }

            ConfigurableItemHandler.items.forEach { (index, itemStack) ->
                player.inventory.setItem(index, itemStack)
            }

            VisibilityHandler.updateAllTo(player)
            VisibilityHandler.updateToAll(player)
        }

        if (settings.allowDoubleJump) {
            Events.subscribe(PlayerToggleFlightEvent::class.java)
                .filter { it.player.gameMode == GameMode.ADVENTURE }
                .handler {
                    val vector = it.player.location.direction
                        .multiply(2.5).setY(1.1)
                    it.isCancelled = true

                    it.player.velocity = vector
                }
        }

        startListeningToItems()

        server.pluginManager.registerEvents(WorldListener, this)
    }

    fun startListeningToItems() {
        ConfigurableItemHandler.items.values
            .filter { it != null && it.hasItemMeta() && it.itemMeta.displayName.contains("Hub Selector") }
            .forEach { stack ->
                Events.subscribe(PlayerInteractEvent::class.java)
                    .filter { it.action.name.contains("RIGHT") }
                    .filter { it.item.isSimilar(stack!!) }
                    .handler {
                        LobbySelectorMenu(
                            ConfigurableMenuHandler.templates["hub-selector"]!!
                        ).openMenu(it.player)
                    }
            }

        ConfigurableItemHandler.items.values
            .filter { it != null && it.hasItemMeta() && it.itemMeta.displayName.contains("Server Selector") }
            .forEach { stack ->
                Events.subscribe(PlayerInteractEvent::class.java)
                    .filter { it.action.name.contains("RIGHT") }
                    .filter { it.item.isSimilar(stack!!) }
                    .handler {
                        ConfigurableMenuHandler.openMenu(it.player, "server-selector")
                    }
            }
    }
}
