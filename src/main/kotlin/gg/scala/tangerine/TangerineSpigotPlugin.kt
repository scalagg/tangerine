package gg.scala.tangerine

import com.cryptomorin.xseries.XSound
import gg.scala.aware.thread.AwareThreadContext
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerDisable
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.lemon.Lemon
import gg.scala.lemon.handler.ChatHandler
import gg.scala.lemon.handler.RedisHandler
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.gaming.CgsGameInfoUpdater
import gg.scala.tangerine.items2.Items2Config
import gg.scala.tangerine.module.HubModule
import gg.scala.tangerine.queue.QueuePlatform
import gg.scala.tangerine.queue.impl.DefaultQueuePlatform
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
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

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

    var platform: QueuePlatform = DefaultQueuePlatform

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
