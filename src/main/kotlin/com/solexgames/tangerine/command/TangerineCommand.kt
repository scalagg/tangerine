package com.solexgames.tangerine.command

import net.evilblock.cubed.acf.BaseCommand
import net.evilblock.cubed.acf.CommandHelp
import net.evilblock.cubed.acf.annotation.CommandAlias
import net.evilblock.cubed.acf.annotation.Default
import net.evilblock.cubed.acf.annotation.HelpCommand

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
@CommandAlias("tangerine|tgr")
class TangerineCommand : BaseCommand() {

    @Default
    @HelpCommand
    fun onHelp(help: CommandHelp) {
        help.showHelp()
    }
}
