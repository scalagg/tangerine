package gg.scala.tangerine

import com.cryptomorin.xseries.XSound
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.core.plugin.Plugin
import gg.scala.commons.core.plugin.PluginAuthor
import gg.scala.commons.core.plugin.PluginDependency
import gg.scala.commons.core.plugin.PluginDependencyComposite
import gg.scala.commons.core.plugin.PluginWebsite
import gg.scala.lemon.Lemon
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.metadata.NetworkMetadataDataSync
import gg.scala.lemon.util.QuickAccess
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.gaming.CgsGameInfoUpdater
import gg.scala.tangerine.items2.Items2Config
import gg.scala.tangerine.module.HubModule
import gg.scala.tangerine.music.Playlist
import gg.scala.tangerine.queue.QueuePlatform
import gg.scala.tangerine.queue.impl.DefaultQueuePlatform
import gg.scala.tangerine.scoreboard.TangerineScoreboardAdapter
import gg.scala.tangerine.settings.TangerineSettings
import gg.scala.tangerine.tablist.TangerineTablistProvider
import gg.scala.tangerine.visibility.TangerineVisibilityAdapter
import gg.tropic.game.extensions.cosmetics.EquipOnLoginCosmeticService
import gg.scala.tangerine.music.Music
import io.github.nosequel.tab.shared.TabHandler
import io.papermc.lib.PaperLib
import me.lucko.helper.Events
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.cubed.serializers.Serializers.useGsonBuilderThenRebuild
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.metadata.FixedMetadataValue

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
@Plugin(
    name = "Tangerine",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthor("Scala")
@PluginWebsite("https://scala.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("ScQueue", soft = true),
    PluginDependency("CoreGameExtensions", soft = true),
    PluginDependency("Placeholder-API", soft = true)
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
        get() = config()

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

        ScoreboardHandler.configure(
            hubModule?.scoreboardAdapter
                ?: TangerineScoreboardAdapter()
        )

        this.registerListeners()

        ConfigurableMenuHandler.reloadMenus()
    }

    private fun registerListeners()
    {
        if (NetworkMetadataDataSync.serverName() != "Minequest")
        {
            Events
                .subscribe(
                    PlayerDropItemEvent::class.java
                )
                .handler { it.isCancelled = true }
        }

        Events
            .subscribe(PlayerInteractEvent::class.java)
            .handler {
                if (it.action == Action.RIGHT_CLICK_AIR)
                {
                    if (it.hasItem() && it.item.type.name.contains("SKULL"))
                    {
                        it.player.chat("/profile")
                    }
                }
            }

        if (Bukkit.getPluginManager().isPluginEnabled("CoreGameExtensions"))
        {
            EquipOnLoginCosmeticService.defaultFunctionality = false
        }

        Events.subscribe(
            PlayerJoinEvent::class.java,
            EventPriority.HIGHEST
        ).handler { event ->
            val player = event.player

            player.inventory.clear()
            player.inventory.armorContents = arrayOfNulls(4)

            player.health = 20.0
            player.foodLevel = 20
            player.gameMode = GameMode.ADVENTURE

            player.walkSpeed = 0.4F

            player.allowFlight = settings.allowDoubleJump
            player.inventory.heldItemSlot = settings.playerLoginHeldItemSlot

            if (settings.musicEnabled)
            {
                val playlist = Playlist(
                    musicDiscs = settings.musicPlaylist
                        .map(Music::valueOf)
                )
                playlist.play(player)

                player.setMetadata(
                    "playlist",
                    FixedMetadataValue(this, playlist)
                )
            }

            if (settings.spawnLocation != null)
            {
                if (NetworkMetadataDataSync.serverName() != "Minequest")
                {
                    PaperLib.teleportAsync(player, settings.spawnLocation!!)
                }
            }

            if (settings.joinMessage.isNotEmpty())
            {
                settings.joinMessage
                    .forEach {
                        player.sendMessage(Color.translate(it))
                    }
            }

            if (hubModule == null)
            {
                Tasks.delayed(2L) {
                    config<Items2Config>().items
                        .forEach { model ->
                            if (model.item.type == Material.SKULL_ITEM)
                            {
                                player.inventory.setItem(
                                    model.position,
                                    ItemBuilder
                                        .copyOf(model.item)
                                        .owner(player.name)
                                        .build()
                                )
                                return@forEach
                            }

                            player.inventory.setItem(model.position, model.item)
                        }

                    player.updateInventory()

                    if (player.hasPermission("tangerine.join-notifs") && !player.hasMetadata("vanished"))
                    {
                        val lemonPlayer = PlayerHandler
                            .find(player.uniqueId)
                            ?: return@delayed

                        Bukkit.broadcastMessage(
                            "${
                                lemonPlayer
                                    .getColoredName(
                                        rank = lemonPlayer.disguiseRank() ?: QuickAccess.realRank(player),
                                        customColor = false,
                                        prefixIncluded = true
                                    )
                            }${CC.YELLOW} joined the lobby!"
                        )
                    }
                }
            }

            if (Bukkit.getPluginManager().isPluginEnabled("CoreGameExtensions"))
            {
                EquipOnLoginCosmeticService.callOnLoginFor(player)
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
}
