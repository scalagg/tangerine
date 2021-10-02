package gg.scala.tangerine.tab

import gg.scala.lemon.handler.PlayerHandler
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import io.github.nosequel.tab.shared.entry.TabElement
import io.github.nosequel.tab.shared.entry.TabElementHandler
import org.bukkit.entity.Player

class TangerineTablistProvider : TabElementHandler {

    override fun getElement(player: Player): TabElement {
        val element = TabElement()
        val settings = TangerineSpigotPlugin.instance.settings;

        // you could maybe do like arrayOf(left, middle, right).forEachIndexed and
        // then do this stuff, but that would be slower!!! so i did this!!!
        settings.tabLeft.forEachIndexed { index, string ->
            element.add(0, index, this.translateLine(string, player))
        }

        settings.tabMiddle.forEachIndexed { index, string ->
            element.add(1, index, this.translateLine(string, player))
        }

        settings.tabRight.forEachIndexed { index, string ->
            element.add(2, index, this.translateLine(string, player))
        }

        return element
    }

    private fun translateLine(line: String, player: Player): String {
        val lemonPlayer = PlayerHandler.findPlayer(player).orElse(null)

        return line
            .replace("<rank>", lemonPlayer.activeGrant!!.getRank().getColoredName())
            .replace("<players>", TangerinePlayerTracker.globalPlayers.toString())
    }
}