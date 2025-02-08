package gg.scala.tangerine.configurable.editor

import com.cryptomorin.xseries.XMaterial
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.configurable.ConfigurableMenuTemplate
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.menus.ConfirmMenu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
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

    override fun getGlobalButtons(player: Player): Map<Int, Button> {
        val buttons = super.getGlobalButtons(player)
            ?.toMutableMap() ?: mutableMapOf()

        buttons[4] = ItemBuilder(Material.NETHER_STAR)
            .name("${CC.PRI}Create Menu")
            .addToLore(
                CC.GRAY + "Create a new menu",
                "",
                CC.GREEN + "Click to create!"
            )
            .toButton { _, _ ->
                player.sendMessage("${CC.RED}Coming soon.")
            }

        return buttons
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
                    "${CC.YELLOW}Click to view details.",
                    "${CC.YELLOW}Shift Click to delete.",
                )
                .build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            if (clickType.isShiftClick) {
                ConfirmMenu(
                    title = "Are you sure?",
                    confirm = true,
                    callback = {
                        if (it) {
                            ConfigurableMenuHandler.templates.remove(template.id)
                            ConfigurableMenuHandler.saveData()

                            player.sendMessage("${CC.RED}You've deleted the ${CC.YELLOW}${template.id}${CC.RED} template.")
                        }

                        ConfigurableMenuEditor().openMenu(player)
                    }
                )
            } else {
                ConfigurableMenuEditorDetailedView(template).openMenu(player)
            }
        }
    }
}
