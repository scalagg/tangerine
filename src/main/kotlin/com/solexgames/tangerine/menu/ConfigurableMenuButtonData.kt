package com.solexgames.tangerine.menu

import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
data class ConfigurableMenuButtonData(
    val itemStack: ItemStack,
    val menuButtonType: ConfigurableMenuButtonType,
    val menuButtonTypeData: String
)
