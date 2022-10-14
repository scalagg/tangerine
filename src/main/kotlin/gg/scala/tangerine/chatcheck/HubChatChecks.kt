package gg.scala.tangerine.chatcheck

import gg.scala.lemon.channel.annotation.PlayerChatEventCheck
import gg.scala.tangerine.TangerineSpigotPlugin
import net.evilblock.cubed.util.CC
import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * @author GrowlyX
 * @since 10/10/2022
 */
@PlayerChatEventCheck
object HubChatChecks
{
    fun handle(event: AsyncPlayerChatEvent): Pair<String, Boolean>
    {
        if (
            !TangerineSpigotPlugin.instance.settings.allowPlayerChat &&
            !event.player.hasPermission("tangerine.chat-bypass")
        )
        {
            return Pair("${CC.RED}You're not allowed to talk in the hub.", true)
        }

        return Pair("", false)
    }
}
