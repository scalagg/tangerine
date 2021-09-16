package com.solexgames.tangerine.scoreboard

import com.solexgames.lemon.Lemon
import com.solexgames.tangerine.settings.TangerineSettings
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
        val lemonPlayer = Lemon.instance.playerHandler
            .findPlayer(player).orElse(null)

        settings.scoreboard.forEach {
            board.add(
                it
                    .replace("<rank>", lemonPlayer.activeGrant!!.getRank().getColoredName())
                    .replace("<players>", "0")
            )
        }
    }

}
