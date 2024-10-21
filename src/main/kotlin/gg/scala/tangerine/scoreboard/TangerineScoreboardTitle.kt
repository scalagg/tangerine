package gg.scala.tangerine.scoreboard

import gg.scala.commons.scoreboard.TextAnimator
import gg.scala.commons.scoreboard.animations.TextFadeAnimation
import gg.scala.flavor.service.Close
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.settings.TangerineSettings
import net.evilblock.cubed.util.CC
import org.bukkit.ChatColor

/**
 * @author Topu
 * @date 10/21/2024
 */
@Service
object TangerineScoreboardTitle
{
    private val settings: TangerineSettings
        get() = TangerineSpigotPlugin.instance.settings

    private val titleAnimator = TextAnimator.of(
        TextFadeAnimation(
            "${CC.BOLD}${settings.title}",
            ChatColor.GOLD,
            ChatColor.YELLOW,
            ChatColor.DARK_GRAY
        )
    )

    fun getCurrentTitle(): String = titleAnimator.current().text

    @Configure
    fun configure()
    {
        titleAnimator.schedule()
    }

    @Close
    fun close()
    {
        titleAnimator.dispose()
    }
}
