package com.solexgames.tangerine

import com.google.gson.LongSerializationPolicy
import com.solexgames.lemon.Lemon
import com.solexgames.lemon.adapt.LemonPlayerAdapter
import com.solexgames.lemon.adapt.UUIDAdapter
import com.solexgames.lemon.player.LemonPlayer
import com.solexgames.lemon.util.QuickAccess.startListening
import com.solexgames.tangerine.items.ConfigurableItemHandler
import com.solexgames.tangerine.listener.WorldListener
import com.solexgames.tangerine.menu.ConfigurableMenuHandler
import com.solexgames.tangerine.module.HubModule
import com.solexgames.tangerine.scoreboard.TangerineScoreboardOverride
import com.solexgames.tangerine.settings.TangerineSettings
import com.solexgames.tangerine.tracking.TangerinePlayerTracker
import com.solexgames.tangerine.visibility.TangerineVisibilityAdapter
import com.solexgames.tangerine.visibility.TangerineVisibilityOverride
import io.papermc.lib.PaperLib
import me.lucko.helper.Events
import me.lucko.helper.plugin.ExtendedJavaPlugin
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
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Entity
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

    lateinit var settings: TangerineSettings

    lateinit var configurableMenuHandler: ConfigurableMenuHandler
    lateinit var configurableItemHandler: ConfigurableItemHandler

    lateinit var playerTracker: TangerinePlayerTracker

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

        configurableMenuHandler = ConfigurableMenuHandler
        configurableMenuHandler.initialLoad()

        configurableItemHandler = ConfigurableItemHandler
        configurableItemHandler.initialLoad()

        playerTracker = TangerinePlayerTracker

        this.registerListeners()
        this.registerCommands()
    }

    private fun registerCommands() {
        val commandManager = CubedCommandManager(
            plugin = this,
            primary = ChatColor.valueOf(Lemon.instance.lemonWebData.primary),
            secondary = ChatColor.valueOf(Lemon.instance.lemonWebData.secondary)
        )

        Lemon.instance.registerCompletionsAndContexts(commandManager)
        Lemon.instance.registerCommandsInPackage(commandManager, "com.solexgames.tangerine.command")

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

            configurableItemHandler.items.forEach { (index, itemStack) ->
                player.inventory.setItem(index, itemStack)
            }
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

        configurableItemHandler.items.values
            .filter { it != null && it.hasItemMeta() && it.itemMeta.displayName.contains("Hub Selector") }
            .forEach {
                startListening(it!!) { event ->
                    configurableMenuHandler.openMenu(event.player, "hub-selector")
                }
            }

        configurableItemHandler.items.values
            .filter { it != null && it.hasItemMeta() && it.itemMeta.displayName.contains("Server Selector") }
            .forEach {
                startListening(it!!) { event ->
                    configurableMenuHandler.openMenu(event.player, "server-selector")
                }
            }

        server.pluginManager.registerEvents(WorldListener, this)
    }
}
