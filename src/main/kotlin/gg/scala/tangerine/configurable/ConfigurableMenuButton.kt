package gg.scala.tangerine.configurable

import gg.scala.tangerine.placeholder.PlaceholderHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bukkit.ColorUtil
import org.bukkit.ChatColor
import org.bukkit.Material
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

    override fun getMaterial(player: Player): Material {
        return data.itemStack.type
    }

    override fun getDamageValue(player: Player): Byte {
        return data.itemStack.data.data
    }

    override fun getAmount(player: Player): Int {
        return data.itemStack.amount
    }

    override fun getName(player: Player): String {
        return data.itemStack.itemMeta.displayName
    }

    override fun getDescription(player: Player): List<String> {
        val originalLore = data.itemStack.itemMeta.lore

        return PlaceholderHandler.findProvider()?.handle(player, originalLore) ?: originalLore
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        data.menuButtonType.handle.invoke(player, data.menuButtonTypeData)
    }
}
