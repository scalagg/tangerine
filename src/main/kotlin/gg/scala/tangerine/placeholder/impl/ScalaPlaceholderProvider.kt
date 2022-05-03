package gg.scala.tangerine.placeholder.impl

import gg.scala.commons.replacement.Replacements
import gg.scala.tangerine.placeholder.PlaceholderProvider
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 5/2/2022
 */
object ScalaPlaceholderProvider : PlaceholderProvider
{
    override fun handle(
        player: Player,
        list: List<String>
    ): MutableList<String>
    {
        val replaced =
            mutableListOf<String>()

        for (message in list)
        {
            replaced.add(
                Replacements.apply(message, player)
            )
        }

        return replaced
    }

    override fun handle(
        player: Player,
        list: String
    ): String
    {
        return Replacements
            .apply(list, player)
    }
}
