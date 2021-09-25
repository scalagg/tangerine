package gg.scala.tangerine.configurable.editor

import gg.scala.tangerine.configurable.ConfigurableMenuButtonData
import gg.scala.tangerine.configurable.ConfigurableMenuTemplate
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks.delayed
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
class ConfigurableMenuEditorDetailedView(
    private val template: ConfigurableMenuTemplate
) : Menu() {

    override fun getTitle(player: Player): String {
        return "Viewing (${template.title})"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also {
            template.buttons.forEach { entry ->
                it[entry.key] = ConfigurableMenuEditButton(entry.value, template)
            }
        }
    }

    override fun size(buttons: Map<Int, Button>): Int {
        return template.size
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            delayed(1L) {
                ConfigurableMenuEditor().openMenu(player)
            }
        }
    }

    class ConfigurableMenuEditButton(
        private val data: ConfigurableMenuButtonData,
        private val template: ConfigurableMenuTemplate
    ) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(data.itemStack.clone())
                .addToLore(
                    "${CC.GRAY}${CC.STRIKE_THROUGH}-----------------",
                    "${CC.YELLOW}Click to edit button details."
                ).build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            ConfigurableMenuEditorDetailedButtonView(data, template).openMenu(player)
        }
    }

}
