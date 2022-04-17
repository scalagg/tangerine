package gg.scala.tangerine.customizer

import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.configurable.ConfigurableMenuOutput
import net.evilblock.cubed.acf.ConditionFailedException
import net.evilblock.cubed.command.manager.CubedCommandManager
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 4/17/2022
 */
object TangerineCommandCustomizer
{
    @CommandManagerCustomizer
    fun customize(
        commandManager: CubedCommandManager
    )
    {
        commandManager.commandContexts.registerContext(ConfigurableMenuOutput::class.java) {
            val firstArg = it.popFirstArg()

            val template = ConfigurableMenuHandler.templates[firstArg]
                ?: throw ConditionFailedException("No menu by the name ${CC.YELLOW}$firstArg${CC.RED} was found.")

            return@registerContext ConfigurableMenuOutput(template)
        }
    }
}
