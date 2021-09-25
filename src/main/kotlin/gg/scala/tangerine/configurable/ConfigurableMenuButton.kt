package gg.scala.tangerine.configurable

import gg.scala.tangerine.placeholder.PlaceholderHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bukkit.ColorUtil
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
open class ConfigurableMenuButton(
    private val data: ConfigurableMenuButtonData
) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        return data.itemStack
    }

    override fun getDescription(player: Player): List<String> {
        val originalLore = data.itemStack.itemMeta.lore

        return PlaceholderHandler.findProvider()?.handle(player, originalLore) ?: originalLore
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        data.menuButtonType.handle.invoke(player, data.menuButtonTypeData)
    }
}
