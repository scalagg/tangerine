package gg.scala.tangerine.util.impl

import gg.scala.tangerine.util.PlaceholderProvider
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/24/2021
 */
class PapiPlaceholderProvider : PlaceholderProvider {

    override fun handle(player: Player, list: List<String>): MutableList<String> {
        return PlaceholderAPI.setPlaceholders(player, list)
    }
}
