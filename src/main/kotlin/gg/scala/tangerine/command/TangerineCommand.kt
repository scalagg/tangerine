package gg.scala.tangerine.command

import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.items.ConfigurableItemHandler
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.configurable.editor.ConfigurableMenuEditor
import gg.scala.tangerine.placeholder.extension.TangerineServerPlaceholderExpansion
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.CommandHelp
import net.evilblock.cubed.acf.annotation.*
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player
import xyz.mkotb.configapi.ConfigFactory

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
@CommandAlias("tangerine|tgr")
@CommandPermission("tangerine.command.tangerine")
class TangerineCommand : BaseCommand() {

    val configFactory: ConfigFactory = ConfigFactory.newFactory(TangerineSpigotPlugin.instance)

    @Default
    @HelpCommand
    @Syntax("[help]")
    fun onHelp(help: CommandHelp) {
        help.showHelp()
    }

    @Subcommand("editor")
    fun onMenuEditor(player: Player) {
        ConfigurableMenuEditor().openMenu(player)
    }

    @Subcommand("setspawn")
    fun onSetSpawn(player: Player) {
        TangerineSpigotPlugin.instance.settings.spawnLocation = player.location
        configFactory.save("settings", TangerineSpigotPlugin.instance.settings)

        player.sendMessage("${CC.GREEN}You've set the lobby spawn.")
    }

    @Subcommand("contents")
    fun onContents(player: Player)
    {
        player.sendMessage("cached=" + TangerineServerPlaceholderExpansion.cachedServers.size)
        player.sendMessage("runnables=" + TangerineServerPlaceholderExpansion.serverRunnables.size)
    }

    @Subcommand("reload")
    fun onReload(player: Player) {
        ConfigurableMenuHandler.reloadMenus()

        TangerineSpigotPlugin.reload()
        ConfigurableItemHandler.reloadItems()

        player.sendMessage("${CC.GREEN}You've reloaded all configurations.")
    }
}
