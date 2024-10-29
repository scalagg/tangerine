package gg.scala.tangerine.command

import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.flavor.inject.Inject
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.configurable.ConfigurableMenuOutput
import gg.scala.commons.acf.BaseCommand
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.acf.annotation.Syntax
import gg.scala.commons.event.impl.ConfigWatchEvent
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.gaming.CgsGameInfoDisplayMenu
import gg.scala.tangerine.gaming.CgsGameType
import gg.scala.tangerine.menu.LobbySelectorMenu
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
@AutoRegister
object TangerineConfigCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: TangerineSpigotPlugin

    @CommandAlias("testing-hub-selector-render")
    @CommandPermission("op")
    fun onHubSelector(sender: Player)
    {
        LobbySelectorMenu(
            ConfigurableMenuHandler.templates["hub-selector"]!!
        ).openMenu(sender)

        sender.sendMessage("opened menu")
    }

    @CommandAlias("switchlobby|switchlobbies|lobbies")
    @CommandPermission("tangerine.command.switchlobbies")
    fun onSwitchLobbies(sender: Player)
    {
        LobbySelectorMenu(
            ConfigurableMenuHandler.templates["hub-selector"]!!
        ).openMenu(sender)
    }

    @CommandAlias("tangerineconfig|tgrconfig")
    @CommandPermission("tangerine.command.config")
    fun onTangerineConfig(sender: CommandSender)
    {
        sender.sendMessage("${CC.B_PRI}Tangerine Config:")
        sender.sendMessage("")

        sender.sendMessage("${CC.GRAY}Join Message:")
        plugin.settings.joinMessage.forEach {
            sender.sendMessage(" ${CC.GRAY} - $it")
        }

        sender.sendMessage("${CC.GRAY}Scoreboard Lines:")
        plugin.settings.scoreboard.forEach {
            sender.sendMessage(" ${CC.GRAY} - $it")
        }

        sender.sendMessage("${CC.GRAY}Scoreboard Title: ${plugin.settings.title}")

        sender.sendMessage("${CC.GRAY}Double Jump: ${CC.WHITE}${plugin.settings.allowDoubleJump}")
        sender.sendMessage("${CC.GRAY}Player Hiding: ${CC.WHITE}${plugin.settings.autoHidePlayers}")
    }

    @CommandAlias("menu")
    @CommandPermission("tangerine.command.menu")
    fun onMenu(
        player: Player,
        menu: ConfigurableMenuOutput
    )
    {
        menu.openMenu(player)
    }

    @CommandAlias("gamemenu")
    @CommandPermission("tangerine.command.menu")
    fun onGameMenu(
        player: Player,
        gameType: CgsGameType
    )
    {
        CgsGameInfoDisplayMenu(gameType).openMenu(player)
    }
}
