package gg.scala.tangerine.placeholder

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/24/2021
 */
interface PlaceholderProvider {

    fun handle(player: Player?, list: List<String>): MutableList<String>
    fun handle(player: Player, list: String): String

}
