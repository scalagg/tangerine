package gg.scala.tangerine.scoreboard

import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.handler.RankHandler
import gg.scala.tangerine.TangerineSpigotPlugin
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
class TangerineScoreboardAdapter : ScoreboardAdapter() {

    private val platform = TangerineSpigotPlugin.instance.platform

    private val settings: TangerineSettings
        get() = TangerineSpigotPlugin.instance.settings

    override fun getTitle(player: Player): String {
        return settings.title
    }

    override fun getLines(board: LinkedList<String>, player: Player) {
        val hubModule = TangerineSpigotPlugin.instance.hubModule

        if (hubModule != null)
        {
            return hubModule.scoreboardAdapter.getLines(board, player)
        }

        val lemonPlayer = PlayerHandler.findPlayer(player).orElse(null)

        if (platform.isInQueue(player))
        {
            val placeholderReplaced = PlaceholderHandler.findProvider()?.handle(player, settings.scoreboardQueued)
                ?: settings.scoreboardQueued

            placeholderReplaced.forEach {
                board.add(
                    it
                        .replace("<rank>", (lemonPlayer.activeGrant?.getRank() ?: RankHandler.getDefaultRank()).getColoredName())
                        .replace("<players>", String.format("%,d", TangerinePlayerTracker.globalPlayers))
                        .replace("<queue_name>", platform.getQueueName(player))
                        .replace("<queue_position>", platform.getQueuePosition(player).toString())
                        .replace("<queue_maximum>", platform.getQueuePlayers(player).toString())
                )
            }
        } else
        {
            val placeholderReplaced = PlaceholderHandler.findProvider()?.handle(player, settings.scoreboard)
                ?: settings.scoreboard

            placeholderReplaced.forEach {
                board.add(
                    it
                        .replace("<rank>", (lemonPlayer.activeGrant?.getRank() ?: RankHandler.getDefaultRank()).getColoredName())
                        .replace("<players>", String.format("%,d", TangerinePlayerTracker.globalPlayers))
                )
            }
        }
    }

}
