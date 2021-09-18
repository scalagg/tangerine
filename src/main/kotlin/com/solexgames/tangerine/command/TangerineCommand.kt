package com.solexgames.tangerine.command

import com.solexgames.tangerine.TangerineSpigotPlugin
import com.solexgames.tangerine.items.ConfigurableItemHandler
import com.solexgames.tangerine.menu.ConfigurableMenuHandler
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.CommandHelp
import net.evilblock.cubed.acf.annotation.*
import net.evilblock.cubed.util.CC
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
@CommandAlias("tangerine|tgr")
class TangerineCommand : BaseCommand() {

    @Default
    @HelpCommand
    @Syntax("[help]")
    fun onHelp(help: CommandHelp) {
        help.showHelp()
    }

    @Subcommand("setspawn")
    fun onSetSpawn(player: Player) {
        TangerineSpigotPlugin.instance.settings.spawnLocation = player.location
        TangerineSpigotPlugin.instance.configFactory.save("settings", TangerineSpigotPlugin.instance.settings)

        player.sendMessage("${CC.GREEN}You've set the lobby spawn.")
    }

    @Subcommand("reload")
    fun onReload(player: Player) {
        ConfigurableMenuHandler.reloadMenus()

        TangerineSpigotPlugin.reloadConfig()
        ConfigurableItemHandler.reloadItems()

        player.sendMessage("${CC.GREEN}You've reloaded all configurations.")
    }
}
