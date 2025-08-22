package gg.scala.tangerine.scoreboard

import gg.scala.game.extensions.profile.CorePlayerProfileService
import gg.scala.lemon.Lemon
import gg.scala.lemon.LemonConstants
import gg.scala.lemon.animated.AnimatedScoreboardTitle
import gg.scala.lemon.handler.PlayerHandler
import gg.scala.lemon.handler.RankHandler
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.settings.TangerineSettings
import gg.scala.tangerine.tracking.TangerinePlayerTracker
import gg.scala.tangerine.placeholder.PlaceholderHandler
import net.evilblock.cubed.scoreboard.ScoreboardAdapter
import net.evilblock.cubed.util.math.Numbers
import org.bukkit.entity.Player
import java.util.*

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
class TangerineScoreboardAdapter : ScoreboardAdapter()
{
    private val settings: TangerineSettings
        get() = TangerineSpigotPlugin.instance.settings

    override fun getTitle(player: Player): String
    {
        val hubModule = TangerineSpigotPlugin.instance.hubModule

        if (hubModule != null)
        {
            return hubModule.scoreboardAdapter.getTitle(player)
        }

        if (settings.titleAnimation) {
            return ScoreboardAnimationService.getCurrentTitle()
        }

        return settings.title
    }

    override fun getLines(board: LinkedList<String>, player: Player)
    {
        val hubModule = TangerineSpigotPlugin.instance.hubModule

        if (hubModule != null)
        {
            return hubModule.scoreboardAdapter.getLines(board, player)
        }

        val lemonPlayer = PlayerHandler
            .find(player.uniqueId)
            ?: return

        val rank = (lemonPlayer.activeGrant?.getRank() ?: RankHandler.getDefaultRank())

        if (TangerineSpigotPlugin.instance.platform.isInQueue(player))
        {
            val placeholderReplaced = PlaceholderHandler.findProvider()
                .handle(player, settings.scoreboardQueued)

            placeholderReplaced.forEach {
                board.add(
                    it
                        .replace(
                            "<rank>",
                            (lemonPlayer.activeGrant?.getRank() ?: RankHandler.getDefaultRank()).getColoredName()
                        )
                        .replace("<players>", String.format("%,d", TangerinePlayerTracker.globalPlayers))
                        .replace("<hubServer>", Lemon.instance.settings.id.replace("hub-", ""))
                        .replace("<coins>", Numbers.format(CorePlayerProfileService.find(player)!!.coins))
                        .replace("<level>", Numbers.format(CorePlayerProfileService.find(player)!!.level))
                        .replace("<rankPrefix>", if (rank.name == "Default") "Default" else rank.prefix)
                        .replace("<queue_name>", TangerineSpigotPlugin.instance.platform.getQueueName(player))
                        .replace("<queue_position>", Numbers.format(TangerineSpigotPlugin.instance.platform.getQueuePosition(player)))
                        .replace("<queue_maximum>", Numbers.format(TangerineSpigotPlugin.instance.platform.getQueuePlayers(player)))
                        .replace("<server_name>", LemonConstants.SERVER_NAME)
                        .replace("<server_domain>", LemonConstants.WEB_LINK)
                )
            }
        } else
        {
            val placeholderReplaced = PlaceholderHandler.findProvider()
                .handle(player, settings.scoreboard)

            placeholderReplaced.forEach {
                board.add(
                    it
                        .replace(
                            "<rank>",
                            (lemonPlayer.activeGrant?.getRank() ?: RankHandler.getDefaultRank()).getColoredName()
                        )
                        .replace("<coins>", Numbers.format(CorePlayerProfileService.find(player)!!.coins))
                        .replace("<level>", Numbers.format(CorePlayerProfileService.find(player)!!.level))
                        .replace("<rankPrefix>", if (rank.name == "Default") "Default" else rank.prefix)
                        .replace("<hubServer>", Lemon.instance.settings.id.replace("hub-", ""))
                        .replace("<coins>", Numbers.format(TangerineSpigotPlugin.instance.currencyProvider(player)))
                        .replace("<players>", Numbers.format(TangerinePlayerTracker.globalPlayers))
                        .replace("<server_name>", LemonConstants.SERVER_NAME)
                        .replace("<server_domain>", LemonConstants.WEB_LINK)
                )
            }
        }
    }
}
