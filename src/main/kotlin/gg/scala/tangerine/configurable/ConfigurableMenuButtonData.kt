package gg.scala.tangerine.configurable

import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
data class ConfigurableMenuButtonData(
    val itemStack: ItemStack,
    var menuButtonType: ConfigurableMenuButtonType,
    var menuButtonTypeData: String
)
