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
        val hubModule = TangerineSpigotPlugin.instance.hubModule

        if (hubModule != null)
        {
            return hubModule.scoreboardAdapter.getTitle(player)
        }

        return settings.title
    }

    override fun getLines(board: LinkedList<String>, player: Player) {
        val hubModule = TangerineSpigotPlugin.instance.hubModule

        if (hubModule != null)
        {
            return hubModule.scoreboardAdapter.getLines(board, player)
        }

        val lemonPlayer = PlayerHandler
            .find(player.uniqueId)
            ?: return

        val thing = (lemonPlayer.activeGrant?.getRank() ?: RankHandler.getDefaultRank())

        if (platform.isInQueue(player))
        {
            val placeholderReplaced = PlaceholderHandler.findProvider()
                .handle(player, settings.scoreboardQueued)

            placeholderReplaced.forEach {
                board.add(
                    it
                        .replace("<rank>", (lemonPlayer.activeGrant?.getRank() ?: RankHandler.getDefaultRank()).getColoredName())
                        .replace("<players>", String.format("%,d", TangerinePlayerTracker.globalPlayers))
                        .replace("<coins>", TangerineSpigotPlugin.instance.currencyProvider(player).toString())
                        .replace("<rankPrefix>", if (thing.name == "Default") "Default" else thing.prefix)
                        .replace("<queue_name>", platform.getQueueName(player))
                        .replace("<queue_position>", platform.getQueuePosition(player).toString())
                        .replace("<queue_maximum>", platform.getQueuePlayers(player).toString())
                )
            }
        } else
        {
            val placeholderReplaced = PlaceholderHandler.findProvider()
                .handle(player, settings.scoreboard)

            placeholderReplaced.forEach {
                board.add(
                    it
                        .replace("<rank>", (lemonPlayer.activeGrant?.getRank() ?: RankHandler.getDefaultRank()).getColoredName())
                        .replace("<rankPrefix>", if (thing.name == "Default") "Default" else thing.prefix)
                        .replace("<coins>", TangerineSpigotPlugin.instance.currencyProvider(player).toString())
                        .replace("<players>", String.format("%,d", TangerinePlayerTracker.globalPlayers))
                )
            }
        }
    }

}
