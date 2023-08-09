package gg.tropic.game.extensions.music

import com.cryptomorin.xseries.ReflectionUtils
import net.evilblock.cubed.util.nms.MinecraftProtocol
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedSoundEffect
import org.bukkit.entity.Player

/**
 * Simple utility to play a music disc for a
 *
 * @author GrowlyX
 * @since 7/24/2023
 */
enum class Music(
    val id: String,
    val lengthSeconds: Int
)
{
    Eleven("11", 60 + 11),
    Thirteen("13", 178),
    Blocks("blocks", 345),
    Chirp("chirp", 185),
    Far("far", 174),
    Mall("mall", 180 + 17),
    Mellohi("mellohi", 60 + 36),
    Cat("cat", 185),
    Stal("stal", 120 + 30),
    Strad("strad", 188),
    Wait("wait", 3 * 60 + 58),
    Ward("ward", 4 * 60 + 11);

    fun playFor(player: Player) = ReflectionUtils
        .sendPacket(
            player,
            PacketPlayOutNamedSoundEffect(
                "${
                    if (MinecraftProtocol.getPlayerVersion(player) >= 393) 
                        "music_disc" else "records"
                }.$id",
                player.location.x,
                player.location.y,
                player.location.z,
                1000.0f, 1.0f
            )
        )
}
