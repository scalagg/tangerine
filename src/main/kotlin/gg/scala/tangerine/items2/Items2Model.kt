package gg.scala.tangerine.items2

import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 7/12/2022
 */
data class Items2Model(
    val position: Int,
    val item: ItemStack,
    val action: Items2Action,
    val meta: List<String>
)
