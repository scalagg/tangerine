package gg.scala.tangerine.configurable

import gg.scala.tangerine.placeholder.PlaceholderHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bukkit.ColorUtil
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
open class ConfigurableMenuButton(
    private val data: ConfigurableMenuButtonData
) : Button() {

    override fun getButtonItem(player: Player): ItemStack
    {
        val originalLore = data.itemStack.itemMeta.lore

        return ItemBuilder(data.itemStack.type)
            .data(data.itemStack.data.data.toShort())
            .setLore(PlaceholderHandler.findProvider()?.handle(player, originalLore) ?: originalLore)
            .amount(data.itemStack.amount)
            .addFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE)
            .name(data.itemStack.itemMeta.displayName)
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        data.menuButtonType.handle.invoke(player, data.menuButtonTypeData)
    }
}
