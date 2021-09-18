package gg.scala.tangerine.menu.editor

import com.cryptomorin.xseries.XMaterial
import gg.scala.tangerine.menu.ConfigurableMenuHandler
import gg.scala.tangerine.menu.ConfigurableMenuTemplate
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
class ConfigurableMenuEditor : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String {
        return "Menu Editor ${Constants.DOUBLE_ARROW_RIGHT} Main"
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also {
            ConfigurableMenuHandler.templates.forEach { entry ->
                it[it.size] = TemplateButton(entry.value)
            }
        }
    }

    class TemplateButton(
        private val template: ConfigurableMenuTemplate
    ) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(XMaterial.OAK_SIGN)
                .name("${CC.PRI}${template.id}")
                .addToLore(
                    "${CC.GRAY}Edit this this template.",
                    "",
                    "${CC.GRAY}Title: ${CC.WHITE}${template.title}",
                    "${CC.GRAY}Buttons: ${CC.WHITE}${template.buttons.size}",
                    "${CC.GRAY}Max Size: ${CC.WHITE}${template.size}",
                    "",
                    "${CC.YELLOW}Click to view details."
                )
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            ConfigurableMenuEditorDetailedView(template).openMenu(player)
        }
    }
}
