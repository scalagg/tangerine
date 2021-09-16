package com.solexgames.tangerine

import com.solexgames.lemon.Lemon
import com.solexgames.tangerine.listener.WorldListener
import com.solexgames.tangerine.module.HubModule
import com.solexgames.tangerine.scoreboard.TangerineScoreboardOverride
import com.solexgames.tangerine.settings.TangerineSettings
import com.solexgames.tangerine.visibility.TangerineVisibilityAdapter
import com.solexgames.tangerine.visibility.TangerineVisibilityOverride
import io.papermc.lib.PaperLib
import me.lucko.helper.Events
import me.lucko.helper.plugin.ExtendedJavaPlugin
import net.evilblock.cubed.command.manager.CubedCommandManager
import net.evilblock.cubed.scoreboard.ScoreboardHandler
import net.evilblock.cubed.visibility.VisibilityHandler
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.util.Vector
import xyz.mkotb.configapi.ConfigFactory

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
class TangerineSpigotPlugin : ExtendedJavaPlugin() {

    companion object {
        @JvmStatic
        lateinit var instance: TangerineSpigotPlugin
    }

    lateinit var settings: TangerineSettings

    var hubModule: HubModule? = null

    val configFactory = ConfigFactory.newFactory(this)

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

        server.pluginManager.registerEvents(WorldListener, this)
    }
}
