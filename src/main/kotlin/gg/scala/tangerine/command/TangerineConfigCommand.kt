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
import net.evilblock.cubed.util.CC
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
@AutoRegister
class TangerineConfigCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: TangerineSpigotPlugin

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
        sender.sendMessage("${CC.GRAY}Placeholder Provider: ${CC.WHITE}${plugin.settings.placeholderProvider}")
    }

    @CommandAlias("menu")
    fun onMenu(
        player: Player,
        menu: ConfigurableMenuOutput
    )
    {
        menu.openMenu(player)
    }
}
