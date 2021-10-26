package gg.scala.tangerine

import com.google.gson.LongSerializationPolicy
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.lemon.Lemon
import gg.scala.lemon.adapter.LemonPlayerAdapter
import gg.scala.lemon.adapter.UUIDAdapter
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.notebook.WrappedNotebook
import gg.scala.lemon.player.LemonPlayer
import gg.scala.lemon.player.channel.impl.staff.StaffChannelType
import gg.scala.tangerine.TangerineSpigotConstants.STAFF_PREFIX
import gg.scala.tangerine.agflink.AgfGameInfoUpdater
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.configurable.ConfigurableMenuOutput
import gg.scala.tangerine.items.ConfigurableItemHandler
import gg.scala.tangerine.listener.WorldListener
import gg.scala.tangerine.menu.LobbySelectorMenu
import gg.scala.tangerine.module.HubModule
import gg.scala.tangerine.queue.QueuePlatform
import gg.scala.tangerine.queue.impl.DefaultQueuePlatform
import gg.scala.tangerine.queue.impl.MelonQueuePlatform
import gg.scala.tangerine.scoreboard.TangerineScoreboardAdapter
import gg.scala.tangerine.settings.TangerineSettings
import gg.scala.tangerine.tablist.TangerineTablistProvider
import gg.scala.tangerine.tracking.TangerineLobbyTracker
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import gg.scala.tangerine.updater.NpcTextUpdater
import gg.scala.tangerine.visibility.TangerineVisibilityAdapter
import io.github.nosequel.tab.shared.TabHandler
import io.papermc.lib.PaperLib
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
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
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.util.BlockVector
import xyz.mkotb.configapi.ConfigFactory
import xyz.xenondevs.particle.ParticleEffect
import java.util.*

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
class TangerineSpigotPlugin : ExtendedScalaPlugin() {

    companion object {
        @JvmStatic
        lateinit var instance: TangerineSpigotPlugin

        @JvmStatic
        fun reload() {
        }

        @JvmStatic
        fun useModule(hubModule: HubModule) {
            instance.hubModule = hubModule

            instance.logger.info("Now loading tangerine with custom hub module.")
        }
    }

    var itemTerminable = CompositeTerminable.create()

    lateinit var platform: QueuePlatform
    lateinit var settings: TangerineSettings

    private var hubModule: HubModule? = null

    override fun enable() {
        instance = this

        val configFactory: ConfigFactory = ConfigFactory.newFactory(this)

        settings = configFactory.fromFile(
            "settings", TangerineSettings::class.java
        )

        if (settings.autoHidePlayers) {
            VisibilityHandler.registerAdapter("tangerine", TangerineVisibilityAdapter)
        }

        if (settings.tabEnabled) {
            TabHandler(
                TangerineTablistProvider(), this, 30L
            )
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

        NpcTextUpdater.initialLoad()
        AgfGameInfoUpdater.start()

        Schedulers.async().runRepeating(
            TangerinePlayerTracker, 0L, 20L
        )
        Schedulers.async().runRepeating(
            TangerineLobbyTracker, 0L, 20L
        )

        platform = if (server.pluginManager.getPlugin("Melon") != null)
        {
            MelonQueuePlatform()
        } else {
            DefaultQueuePlatform
        }

        ScoreboardHandler.configure(hubModule?.scoreboardAdapter ?: TangerineScoreboardAdapter(this.settings))

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
        Events.subscribe(PlayerJoinEvent::class.java, EventPriority.LOWEST).handler { event ->
            val player = event.player

            player.inventory.clear()
            player.inventory.armorContents = arrayOfNulls(4)

            player.health = 20.0
            player.foodLevel = 20
            player.gameMode = GameMode.ADVENTURE

            player.walkSpeed = 0.4F
            player.allowFlight = settings.allowDoubleJump

            if (settings.spawnLocation != null) {
                PaperLib.teleportAsync(player, settings.spawnLocation!!)
            }

            if (settings.joinMessage.isNotEmpty()) {
                player.sendMessage(settings.joinMessage.toTypedArray())
            }

            if (settings.shouldSendStaffInfo && player.hasPermission("lemon.staff")) {
                PlayerHandler.findPlayer(player).orElse(null)?.let { lemonPlayer ->
                    val channel = lemonPlayer.getMetadata("channel")

                    player.sendMessage(
                        arrayOf(
                            "${CC.B_PRI}Staff Information:",
                            "${STAFF_PREFIX}${CC.SEC}Channel: ${
                                if (channel == null) CC.RED else StaffChannelType.valueOf(
                                    channel.asString().uppercase(Locale.getDefault())
                                ).color
                            }${
                                (channel?.asString()
                                    ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) ?: "None"
                            }",
                            "${STAFF_PREFIX}${CC.SEC}Messages: ${
                                if (lemonPlayer.getSetting("staff-messages-disabled")) {
                                    "${CC.RED}Hidden"
                                } else {
                                    "${CC.GREEN}Shown"
                                }
                            } ${CC.GRAY}(/tsm)",
                            "${STAFF_PREFIX}${CC.SEC}Reports: ${
                                if (lemonPlayer.getSetting("reports-disabled")) {
                                    "${CC.RED}Hidden"
                                } else {
                                    "${CC.GREEN}Shown"
                                }
                            } ${CC.GRAY}(/trm)",
                            "${STAFF_PREFIX}${CC.SEC}Filter: ${
                                if (lemonPlayer.getSetting("filtered-messages-disabled")) {
                                    "${CC.RED}Hidden"
                                } else {
                                    "${CC.GREEN}Shown"
                                }
                            } ${CC.GRAY}(/tfm)",
                        )
                    )
                }
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

                    ParticleEffect.FLAME.display(it.player.location)
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
                    .filter { it.item != null && it.action.name.contains("RIGHT") }
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
                    .filter { it.item != null && it.action.name.contains("RIGHT") }
                    .filter { it.item.isSimilar(stack!!) }
                    .handler {
                        ConfigurableMenuHandler.openMenu(it.player, "server-selector")
                    }
            }

        val `true` = FixedMetadataValue(this, true)

        ConfigurableItemHandler.items
            .filter { it.value != null && it.value!!.hasItemMeta() && it.value!!.itemMeta.displayName.contains("Players") }
            .forEach { entry ->
                val stack = entry.value

                if (stack != null) {
                    val enabledStack = ItemBuilder(stack.clone())
                        .data(10).build()
                    val disabledStack = ItemBuilder(stack.clone())
                        .data(8).build()

                    Events.subscribe(PlayerInteractEvent::class.java)
                        .filter { it.item != null && it.action.name.contains("RIGHT") }
                        .filter { it.item.hasItemMeta() && it.item.itemMeta.displayName.contains("Players") }
                        .handler {
                            val hidingNow = it.player.hasMetadata("hiding-players")

                            if (hidingNow) {
                                it.player.removeMetadata("hiding-players", this)
                            } else {
                                it.player.setMetadata("hiding-players", `true`)
                            }

                            it.player.inventory.setItem(
                                entry.key,
                                if (hidingNow) {
                                    enabledStack
                                } else {
                                    disabledStack
                                }
                            )
                            it.player.updateInventory()

                            VisibilityHandler.updateAllTo(it.player)
                        }
                }
            }
    }
}
