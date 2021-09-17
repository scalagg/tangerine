package com.solexgames.tangerine.command

import com.solexgames.tangerine.TangerineSpigotPlugin
import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.CommandHelp
import net.evilblock.cubed.acf.annotation.*
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

        player.sendMessage("set spawn")
    }
}
