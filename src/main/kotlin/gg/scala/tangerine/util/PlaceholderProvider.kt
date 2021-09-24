package gg.scala.tangerine.util

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/24/2021
 */
interface PlaceholderProvider {

    fun handle(player: Player, list: List<String>): MutableList<String>

}
