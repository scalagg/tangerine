package gg.scala.tangerine.scoreboard

import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.handler.RankHandler
import gg.scala.tangerine.settings.TangerineSettings
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import gg.scala.tangerine.placeholder.PlaceholderHandler
import net.evilblock.cubed.scoreboard.ScoreboardAdapter
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
class TangerineScoreboardAdapter(
    private val settings: TangerineSettings
) : ScoreboardAdapter() {

    override fun getTitle(player: Player): String {
        return settings.title
    }

    override fun getLines(board: LinkedList<String>, player: Player) {
        val lemonPlayer = PlayerHandler.findPlayer(player).orElse(null)
        val placeholderReplaced = PlaceholderHandler.findProvider()?.handle(player, settings.scoreboard) ?: settings.scoreboard

        placeholderReplaced.forEach {
            board.add(
                it
                    .replace("<rank>", (lemonPlayer.activeGrant?.getRank() ?: RankHandler.getDefaultRank()).getColoredName())
                    .replace("<players>", String.format("%,d", TangerinePlayerTracker.globalPlayers))
            )
        }
    }

}
