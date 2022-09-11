package gg.scala.tangerine.settings

import com.cryptomorin.xseries.XSound
import com.cryptomorin.xseries.particles.XParticle
import net.evilblock.cubed.util.CC
import org.bukkit.Location
import org.bukkit.Sound
import xyz.mkotb.configapi.Coloured
import xyz.mkotb.configapi.RequiredField
import xyz.mkotb.configapi.comment.Comment
import xyz.xenondevs.particle.ParticleEffect

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
    val scoreboardQueued = listOf(
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

    val tabEnabled = false

    val tabLeft = listOf(
        ""
    )

    val tabMiddle = listOf(
        ""
    )

    val tabRight = listOf(
        ""
    )

    @Coloured
    val joinMessage = mutableListOf(
        "${CC.PRI}${CC.STRIKE_THROUGH}-----------------------------------------------",
        "${CC.WHITE}Welcome to the Scala Development Lobby",
        "${CC.PRI}${CC.STRIKE_THROUGH}-----------------------------------------------"
    )

    var spawnLocation: Location? = null

    @Comment("Should we automatically hide players?")
    val autoHidePlayers = false
    val allowDoubleJump = true
    val removeStartupEntities = false

    val allowPlayerChat = true

    val doubleJumpParticle = true
    val doubleJumpParticleType = ParticleEffect.FLAME

    val playerVisibilityCooldown = 1
    val playerVisibilityCooldownUnit = "SECONDS"
    val playerVisibilityCooldownMessage = "${CC.RED}You must wait ${CC.B_RED}%s${CC.RED} seconds before trying again."

    val playerLoginHeldItemSlot = 0

    val doubleJumpSound = true
    val doubleJumpSoundType = Sound.EXPLODE

}
