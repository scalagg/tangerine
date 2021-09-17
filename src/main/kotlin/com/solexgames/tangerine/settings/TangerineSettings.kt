package com.solexgames.tangerine.settings

import com.cryptomorin.xseries.XMaterial
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Location
import xyz.mkotb.configapi.Coloured
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
        "${CC.GRAY}${CC.STRIKE_THROUGH}-----------------",
        "${CC.WHITE}Online:",
        "${CC.PRI}<players>",
        "",
        "${CC.WHITE}Rank:",
        "${CC.PRI}<rank>",
        "",
        "${CC.PRI}solexgames.com",
        "${CC.GRAY}${CC.STRIKE_THROUGH}-----------------"
    )

    @Coloured
    val joinMessage = listOf(
        "${CC.PRI}${CC.STRIKE_THROUGH}-----------------------------------------------",
        "${CC.WHITE}Welcome to the SolexGames Development Lobby",
        "${CC.PRI}${CC.STRIKE_THROUGH}-----------------------------------------------"
    )

    var spawnLocation: Location? = null

    @Comment("Should we automatically hide players?")
    val autoHidePlayers = false
    val allowDoubleJump = true

    val itemStackList = listOf(
        null,
        null,
        null,
        ItemBuilder(XMaterial.COMPASS)
            .name("${CC.B_PRI}Server Selector")
            .addToLore(
                "${CC.GRAY}View all our servers."
            )
            .build(),
        null,
        ItemBuilder(XMaterial.NETHER_STAR)
            .name("${CC.B_PRI}Hub Selector")
            .addToLore(
                "${CC.GRAY}View all our lobbies."
            )
            .build(),
        null,
        null,
        null,
    )

}
