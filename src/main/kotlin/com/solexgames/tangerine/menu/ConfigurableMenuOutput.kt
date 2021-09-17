package com.solexgames.tangerine.menu

import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
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
}
