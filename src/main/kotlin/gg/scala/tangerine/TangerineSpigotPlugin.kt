package gg.scala.tangerine

import com.cryptomorin.xseries.XSound
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.lemon.Lemon
import gg.scala.lemon.channel.channels.staff.StaffChatChannelType
import gg.scala.lemon.handler.ChatHandler
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.handler.RedisHandler
import gg.scala.tangerine.TangerineSpigotConstants.STAFF_PREFIX
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.gaming.CgsGameInfoUpdater
import gg.scala.tangerine.items2.Items2Config
import gg.scala.tangerine.module.HubModule
import gg.scala.tangerine.queue.QueuePlatform
import gg.scala.tangerine.queue.impl.CougarQueuePlatform
import gg.scala.tangerine.scoreboard.TangerineScoreboardAdapter
import gg.scala.tangerine.settings.TangerineSettings
import gg.scala.tangerine.tablist.TangerineTablistProvider
import gg.scala.tangerine.visibility.TangerineVisibilityAdapter
import io.github.nosequel.tab.shared.TabHandler
import io.papermc.lib.PaperLib
import me.lucko.helper.Events
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.cubed.serializers.Serializers.useGsonBuilderThenRebuild
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import java.util.*

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
@Plugin(
    name = "Tangerine",
    depends = [
        PluginDependency("scala-commons"),
        PluginDependency("Lemon"),
        PluginDependency("cougar", soft = true),
        PluginDependency("scduels", soft = true),
        PluginDependency("Placeholder-API", soft = true)
    ]
)
@ContainerConfig(
    value = "settings",
    model = TangerineSettings::class,
    crossSync = true
)
class TangerineSpigotPlugin : ExtendedScalaPlugin()
{
    companion object
    {
        @JvmStatic
        lateinit var instance: TangerineSpigotPlugin
    }

    val platform: QueuePlatform
        get() = CougarQueuePlatform

    val settings: TangerineSettings
        get() = this.config()

    var currencyProvider = { player: Player -> 0 }

    var hubModule: HubModule? = null

    @ContainerEnable
    fun containerEnable()
    {
        instance = this

        if (settings.autoHidePlayers)
        {
            VisibilityHandler.registerAdapter(
                "tangerine", TangerineVisibilityAdapter
            )
        }

        if (settings.tabEnabled)
        {
            TabHandler(
                TangerineTablistProvider, this, 10L
            )
        }

        if (settings.removeStartupEntities)
        {
            Bukkit.getWorlds().forEach {
                it.entities.forEach { entity ->
                    if (entity !is Player)
                    {
                        entity.remove()
                    }
                }
            }
        }

        useGsonBuilderThenRebuild {
            it.setPrettyPrinting()
        }

        CgsGameInfoUpdater.start()

        if (!Lemon.instance.settings.consulEnabled)
        {
            RedisHandler.buildMessage(
                "add-server",
                "id" to Lemon.instance
                    .settings.id,
                "address" to "127.0.0.1",
                "port" to Bukkit.getPort()
                    .toString()
            ).publish(
                channel = "cocoa"
            )
        }

        ScoreboardHandler.configure(
            hubModule?.scoreboardAdapter
                ?: TangerineScoreboardAdapter()
        )

        this.registerListeners()

        ConfigurableMenuHandler.reloadMenus()
    }

    private fun registerListeners()
    {
        val emptyPair = Pair("", false)

        if (
            Lemon.instance.lemonWebData
                .serverName != "Minequest"
        )
        {
            Events
                .subscribe(
                    PlayerDropItemEvent::class.java
                )
                .handler { it.isCancelled = true }
        }

        ChatHandler.registerChatCheck {
            if (
                !settings.allowPlayerChat &&
                !it.player.hasPermission("tangerine.chat-bypass")
            )
            {
                return@registerChatCheck Pair("${CC.RED}You're not allowed to talk in the hub.", true)
            }

            return@registerChatCheck emptyPair
        }

        Events.subscribe(
            PlayerJoinEvent::class.java,
            EventPriority.LOWEST
        ).handler { event ->
            val player = event.player

            player.inventory.clear()
            player.inventory.armorContents = arrayOfNulls(4)

            player.health = 20.0
            player.foodLevel = 20
            player.gameMode = GameMode.ADVENTURE

            player.walkSpeed = if (
                Lemon.instance.lemonWebData
                    .serverName == "Minequest"
            ) 0.2F else 0.4F

            player.allowFlight = settings.allowDoubleJump
            player.inventory.heldItemSlot = settings.playerLoginHeldItemSlot

            if (settings.spawnLocation != null)
            {
                val minequest = Lemon.instance
                    .lemonWebData.serverName == "Minequest"

                if (!minequest)
                {
                    PaperLib.teleportAsync(player, settings.spawnLocation!!)
                }
            }

            if (settings.joinMessage.isNotEmpty())
            {
                settings.joinMessage
                    .forEach {
                        player.sendMessage(it)
                    }
            }

            if (settings.shouldSendStaffInfo && player.hasPermission("lemon.staff"))
            {
                PlayerHandler.findPlayer(player).orElse(null)?.let { lemonPlayer ->
                    val channel = lemonPlayer.getMetadata("channel")
                    val messages = listOf(
                        "${CC.B_PRI}Staff Information:",
                        "${STAFF_PREFIX}${CC.SEC}Channel: ${
                            if (channel == null) CC.RED else ChatColor.valueOf(
                                StaffChatChannelType.valueOf(
                                    channel.asString().uppercase(Locale.getDefault())
                                ).color.toString().uppercase()
                            )
                        }${
                            (channel?.asString()
                                ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) ?: "None"
                        }",
                        "${STAFF_PREFIX}${CC.SEC}Messages: ${
                            if (lemonPlayer.getSetting("staff-messages-disabled"))
                            {
                                "${CC.RED}Hidden"
                            } else
                            {
                                "${CC.GREEN}Shown"
                            }
                        } ${CC.GRAY}(/tsm)",
                        "${STAFF_PREFIX}${CC.SEC}Reports: ${
                            if (lemonPlayer.getSetting("reports-disabled"))
                            {
                                "${CC.RED}Hidden"
                            } else
                            {
                                "${CC.GREEN}Shown"
                            }
                        } ${CC.GRAY}(/trm)",
                        "${STAFF_PREFIX}${CC.SEC}Filter: ${
                            if (lemonPlayer.getSetting("filtered-messages-disabled"))
                            {
                                "${CC.RED}Hidden"
                            } else
                            {
                                "${CC.GREEN}Shown"
                            }
                        } ${CC.GRAY}(/tfm)",
                    )

                    for (message in messages)
                    {
                        player.sendMessage(message)
                    }
                }
            }

            if (hubModule != null)
            {
                hubModule!!.itemAdapter.handle(player)
            } else
            {
                Tasks.delayed(2L) {
                    config<Items2Config>().items
                        .forEach { model ->
                            player.inventory.setItem(model.position, model.item)
                        }

                    player.updateInventory()
                }
            }

            VisibilityHandler.updateAllTo(player)
        }

        if (settings.allowDoubleJump)
        {
            Events.subscribe(PlayerToggleFlightEvent::class.java)
                .filter { it.player.gameMode == GameMode.ADVENTURE }
                .handler {
                    val vector = it.player.location.direction
                        .multiply(2.5).setY(1.1)

                    it.isCancelled = true
                    it.player.velocity = vector

                    if (settings.doubleJumpParticle)
                    {
                        settings.doubleJumpParticleType.display(
                            it.player.location,
                            *Bukkit.getOnlinePlayers()
                                .filter { other -> other.canSee(it.player) }
                                .toTypedArray()
                        )
                    }

                    if (settings.doubleJumpSound)
                    {
                        XSound
                            .matchXSound(settings.doubleJumpSoundType)
                            .play(it.player)
                    }
                }
        }
    }

    @ContainerDisable
    fun containerDisable()
    {
        if (!Lemon.instance.settings.consulEnabled)
        {
            RedisHandler.buildMessage(
                "remove-server",
                "id" to Lemon.instance
                    .settings.id
            ).publish(
                channel = "cocoa",
                context = AwareThreadContext.SYNC
            )
        }
    }
}
