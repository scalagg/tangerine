package gg.scala.tangerine.tablist

import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.placeholder.PlaceholderHandler
import io.github.nosequel.tab.shared.entry.TabElement
import io.github.nosequel.tab.shared.entry.TabElementHandler
import org.bukkit.entity.Player

class TangerineTablistProvider : TabElementHandler {

    override fun getElement(player: Player): TabElement {
        val element = TabElement()
        val settings = TangerineSpigotPlugin.instance.settings
        val placeholder = PlaceholderHandler.findProvider()

        // you could maybe do like arrayOf(left, middle, right).forEachIndexed and
        // then do this stuff, but that would be slower!!! so i did this!!!
        settings.tabLeft.forEachIndexed { index, string ->
            element.add(0, index, placeholder?.handle(player, string) ?: string)
        }

        settings.tabMiddle.forEachIndexed { index, string ->
            element.add(1, index, placeholder?.handle(player, string) ?: string)
        }

        settings.tabRight.forEachIndexed { index, string ->
            element.add(2, index, placeholder?.handle(player, string) ?: string)
        }

        return element
    }
}
