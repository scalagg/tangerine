package gg.scala.tangerine.tablist

import io.github.nosequel.tab.shared.entry.TabElement
import io.github.nosequel.tab.shared.entry.TabElementHandler
import org.bukkit.entity.Player

object TangerineTablistProvider : TabElementHandler
{
    @JvmStatic
    val BLANK = TabElement()

    override fun getElement(player: Player): TabElement
    {
        return TangerineTablistUpdater.cached[player.uniqueId] ?: BLANK
    }
}
