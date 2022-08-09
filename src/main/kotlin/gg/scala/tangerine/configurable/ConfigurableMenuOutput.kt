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
    private val template: String,
    private val fallbackOverride: Boolean = false
) : Menu() {

    init {
        val template = ConfigurableMenuHandler
            .templates[template]!!

        placeholdBorders = template.placeholder
        placeholder = template.placeholderFill

        autoUpdate = template.updateAutomatically
    }

    override fun getTitle(player: Player): String
    {
        val template = ConfigurableMenuHandler
            .templates[template]!!

        return template.title
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also {
            val template = ConfigurableMenuHandler
                .templates[template]!!

            template.buttons.forEach { entry ->
                it[entry.key] = ConfigurableMenuButton(entry.value)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        val template = ConfigurableMenuHandler
            .templates[template]!!

        return template.size
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose)
        {
            val template = ConfigurableMenuHandler
                .templates[template]!!

            if (fallbackOverride)
                return

            template.fallback?.let {
                delayed(1L) {
                    ConfigurableMenuHandler.openMenu(player, it)
                }
            }
        }
    }
}
