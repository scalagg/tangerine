package gg.scala.tangerine.scoreboard

import gg.scala.lemon.handler.PlayerHandler
import gg.scala.tangerine.settings.TangerineSettings
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import gg.scala.tangerine.util.PlaceholderHandler
import net.evilblock.cubed.scoreboard.ScoreboardOverride
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
class TangerineScoreboardOverride(
    private val settings: TangerineSettings
) : ScoreboardOverride() {

    override fun shouldOverride(player: Player): Boolean {
        return true
    }

    override fun getTitle(player: Player): String {
        return settings.title
    }

    override fun getLines(board: LinkedList<String>, player: Player) {
        val lemonPlayer = PlayerHandler.findPlayer(player).orElse(null)
        val placeholderReplaced = PlaceholderHandler.findProvider()?.handle(player, board) ?: board

        placeholderReplaced.forEach {
            board.add(
                it
                    .replace("<rank>", lemonPlayer.activeGrant!!.getRank().getColoredName())
                    .replace("<players>", TangerinePlayerTracker.globalPlayers.toString())
            )
        }
    }

}
