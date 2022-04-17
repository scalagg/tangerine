package gg.scala.tangerine.command

import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.flavor.inject.Inject
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.items.ConfigurableItemHandler
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.configurable.editor.ConfigurableMenuEditor
import gg.scala.tangerine.placeholder.extension.TangerineServerPlaceholderExpansion
import gg.scala.tangerine.settings.TangerineSettings
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
@AutoRegister
@CommandAlias("tangerine|tgr")
@CommandPermission("tangerine.command.tangerine")
object TangerineCommand : ScalaCommand()
{
    @Inject
    lateinit var plugin: TangerineSpigotPlugin

    @Default
    @HelpCommand
    @Syntax("[help]")
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("editor")
    fun onMenuEditor(player: Player)
    {
        ConfigurableMenuEditor().openMenu(player)
    }

    @Subcommand("setspawn")
    fun onSetSpawn(player: Player)
    {
        plugin.settings
            .spawnLocation = player.location

        val factory = ConfigFactory
            .newFactory(plugin)

        factory.save("settings", plugin.settings)

        player.sendMessage("${CC.GREEN}You've set the lobby spawn.")
    }

    @Subcommand("contents")
    fun onContents(player: Player)
    {
        player.sendMessage("cached=" + TangerineServerPlaceholderExpansion.cachedServers.size)
        player.sendMessage("runnables=" + TangerineServerPlaceholderExpansion.serverRunnables.size)
    }

    @Subcommand("reload")
    fun onReload(player: Player)
    {
        ConfigurableMenuHandler.reloadMenus()
        ConfigurableItemHandler.reloadItems()

        plugin.configContainerized.loadAll()

        player.sendMessage("${CC.GREEN}You've reloaded all configurations.")
    }
}
