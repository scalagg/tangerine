package gg.scala.tangerine.menu

import gg.scala.tangerine.menu.editor.ConfigurableMenuEditor
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.bukkit.Tasks
import net.evilblock.cubed.util.bukkit.Tasks.delayed
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
class ConfigurableMenuOutput(
    private val template: ConfigurableMenuTemplate
) : Menu() {

    override fun getTitle(player: Player): String {
        return template.title
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
