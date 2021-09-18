package gg.scala.tangerine.menu

import net.evilblock.cubed.menu.Button
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
class ConfigurableMenuButton(
    private val data: ConfigurableMenuButtonData
) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        return data.itemStack
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
        data.menuButtonType.handle.invoke(player, data.menuButtonTypeData)
    }
}
