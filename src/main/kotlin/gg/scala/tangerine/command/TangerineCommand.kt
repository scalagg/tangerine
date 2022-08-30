package gg.scala.tangerine.command

import gg.scala.commons.acf.CommandHelp
import gg.scala.commons.acf.annotation.*
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import gg.scala.flavor.inject.Inject
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.configurable.editor.ConfigurableMenuEditor
import gg.scala.tangerine.items2.TangerineItems2Service
import gg.scala.tangerine.menu.JoinMessageEditMenu
import me.lucko.helper.Events
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.metadata.FixedMetadataValue
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

    init
    {
        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                it.player.removeMetadata("builder", plugin)
            }
    }

    @Default
    @HelpCommand
    @Syntax("[help]")
    @Description("View command help.")
    fun onHelp(help: CommandHelp)
    {
        help.showHelp()
    }

    @Subcommand("editor")
    @Description("Open the menu editor.")
    fun onMenuEditor(player: Player)
    {
        ConfigurableMenuEditor().openMenu(player)
    }

    @Subcommand("build-mode")
    @Description("Enter build mode.")
    fun onBuildMode(player: Player)
    {
        player.setMetadata(
            "builder",
            FixedMetadataValue(plugin, true)
        )

        player.sendMessage("${CC.GREEN}You've entered build mode.")
    }

    @Subcommand("join-message-editor")
    fun onJoinMessageEdit(player: Player)
    {
        JoinMessageEditMenu(
            plugin.settings.joinMessage
        ).openMenu(player)
    }

    @Subcommand("setspawn")
    @Description("Set the lobby spawn.")
    fun onSetSpawn(player: Player)
    {
        plugin.settings
            .spawnLocation = player.location

        val factory = ConfigFactory
            .newFactory(plugin)

        factory.save("settings", plugin.settings)

        player.sendMessage("${CC.GREEN}You've set the lobby spawn.")
    }

    @Subcommand("reload")
    @Description("Reload configuration files.")
    fun onReload(player: CommandSender)
    {
        ConfigurableMenuHandler.reloadMenus()
        TangerineItems2Service.reload()

        plugin.configContainerized.loadAll()

        player.sendMessage("${CC.D_GREEN}${Constants.CHECK_SYMBOL} ${CC.GREEN}You've reloaded all configurations.")
    }
}
