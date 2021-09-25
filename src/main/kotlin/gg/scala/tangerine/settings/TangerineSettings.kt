package gg.scala.tangerine.settings

import net.evilblock.cubed.util.CC
import org.bukkit.Location
import xyz.mkotb.configapi.Coloured
import xyz.mkotb.configapi.RequiredField
import xyz.mkotb.configapi.comment.Comment

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
class TangerineSettings {

    @Coloured
    val title = "${CC.B_PRI}Hub"

    @Coloured
    val scoreboard = listOf(
        "${CC.GRAY}${CC.STRIKE_THROUGH}------------",
        "${CC.WHITE}Online:",
        "${CC.PRI}<players>",
        "",
        "${CC.WHITE}Rank:",
        "${CC.PRI}<rank>",
        "",
        "${CC.PRI}scala.gg",
        "${CC.GRAY}${CC.STRIKE_THROUGH}------------"
    )

    @Coloured
    val joinMessage = listOf(
        "${CC.PRI}${CC.STRIKE_THROUGH}-----------------------------------------------",
        "${CC.WHITE}Welcome to the Scala Development Lobby",
        "${CC.PRI}${CC.STRIKE_THROUGH}-----------------------------------------------"
    )

    @RequiredField
    val placeholderProvider = "papi"

    var spawnLocation: Location? = null

    @Comment("Should we automatically hide players?")
    val autoHidePlayers = false
    val allowDoubleJump = true
    val shouldSendStaffInfo = true

}
