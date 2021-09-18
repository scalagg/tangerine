package gg.scala.tangerine.command

import gg.scala.tangerine.TangerineSpigotPlugin
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.annotation.CommandAlias
import net.evilblock.cubed.util.CC
import org.bukkit.command.CommandSender

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
class TangerineConfigCommand : BaseCommand() {

    private val tangerineSettings = TangerineSpigotPlugin.instance.settings

    @CommandAlias("tangerineconfig|tgrconfig")
    fun onTangerineConfig(sender: CommandSender) {
        sender.sendMessage("${CC.B_PRI}Tangerine Config:")
        sender.sendMessage("")

        sender.sendMessage("${CC.GRAY}Join Message:")
        tangerineSettings.joinMessage.forEach {
            sender.sendMessage(" ${CC.GRAY} - $it")
        }

        sender.sendMessage("${CC.GRAY}Scoreboard Lines:")
        tangerineSettings.scoreboard.forEach {
            sender.sendMessage(" ${CC.GRAY} - $it")
        }

        sender.sendMessage("${CC.GRAY}Scoreboard Title: ${tangerineSettings.title}")

        sender.sendMessage("${CC.GRAY}Double Jump: ${CC.WHITE}${tangerineSettings.allowDoubleJump}")
        sender.sendMessage("${CC.GRAY}Player Hiding: ${CC.WHITE}${tangerineSettings.autoHidePlayers}")
    }
}
