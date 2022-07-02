package gg.scala.tangerine.configurable

import gg.scala.tangerine.queue.impl.CougarQueuePlatform
import net.evilblock.cubed.util.bungee.BungeeUtil
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
enum class ConfigurableMenuButtonType(
    val handle: (Player, String) -> Unit
) {
    NONE({ _, _ -> }),
    COMMAND({ player, data ->
        player.chat("/$data")
    }),
    QUEUE({ player, data ->
        CougarQueuePlatform.joinQueue(player, data)
    }),
    SEND_SERVER({ player, data ->
        BungeeUtil.sendToServer(player, data)
    }),
    MENU({ player, data ->
        ConfigurableMenuHandler.openMenu(player, data)
    }),
    AGF({ player, data ->
        player.sendMessage("${net.evilblock.cubed.util.CC.SEC}Sending you to a ${net.evilblock.cubed.util.CC.PRI}UHC Meetup${net.evilblock.cubed.util.CC.SEC} lobby server...")

        // TODO: 2/22/2022 join an actual uhcm server
        BungeeUtil.sendToServer(player, "na-uml-1")
    })
}
