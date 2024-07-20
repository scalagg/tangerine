package gg.scala.tangerine.music

import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 8/9/2023
 */
class Playlist(
    val musicDiscs: List<Music>,
    var currentIndex: Int = musicDiscs.indices.random(),
    var loop: Boolean = true
)
{
    fun play(player: Player)
    {
        val disc = musicDiscs
            .getOrNull(currentIndex++)
            ?: run {
                if (loop)
                {
                    currentIndex = 0
                    return@run musicDiscs[0]
                }

                return
            }

        if (player.isOnline)
        {
            disc.playFor(player)

            Tasks.delayed(
                disc.lengthSeconds * 20L
            ) {
                play(player)
            }

            Tasks.delayed(20L) {
                player.sendMessage("${CC.GRAY}(${CC.BOLD}♬${CC.GRAY} now playing: ${CC.WHITE}${disc.id}${CC.GRAY})")
            }
        }
    }
}
