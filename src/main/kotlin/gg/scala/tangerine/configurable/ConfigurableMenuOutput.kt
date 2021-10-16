package gg.scala.tangerine.configurable

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks.delayed
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
open class ConfigurableMenuOutput(
    private val template: ConfigurableMenuTemplate
) : Menu(template.title) {

    init {
        placeholdBorders = template.placeholder
        autoUpdate = template.updateAutomatically
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also {
            template.buttons.forEach { entry ->
                it[entry.key] = ConfigurableMenuButton(entry.value)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return template.size
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        template.fallback?.let {
            delayed(1L) {
                ConfigurableMenuHandler.openMenu(player, it)
            }
        }
    }
}
