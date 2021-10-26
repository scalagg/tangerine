package gg.scala.tangerine.placeholder.impl

import gg.scala.tangerine.placeholder.PlaceholderProvider
import gg.scala.tangerine.placeholder.extension.TangerineServerPlaceholderExpansion
import me.clip.placeholderapi.PlaceholderAPI
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/24/2021
 */
class PapiPlaceholderProvider : PlaceholderProvider {

    init {
        if (TangerineServerPlaceholderExpansion.canRegister()) {
            TangerineServerPlaceholderExpansion.register()
        }
    }

    override fun handle(player: Player?, list: List<String>): MutableList<String> {
        return try {
            PlaceholderAPI.setPlaceholders(player, list)
        } catch (e: Exception) {
            list.toMutableList()
        }
    }

    override fun handle(player: Player, list: String): String {
        return try {
            PlaceholderAPI.setPlaceholders(player, list)
        } catch (e: Exception) {
            list
        }
    }
}
