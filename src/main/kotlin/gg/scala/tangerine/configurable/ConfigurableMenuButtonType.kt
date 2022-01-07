package gg.scala.tangerine.configurable

import gg.scala.tangerine.gaming.CgsGameInfoDisplayMenu
import gg.scala.tangerine.gaming.CgsGameType
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
        player.chat("/joinqueue $data")
    }),
    SEND_SERVER({ player, data ->
        BungeeUtil.sendToServer(player, data)
    }),
    MENU({ player, data ->
        ConfigurableMenuHandler.openMenu(player, data)
    }),
    AGF({ player, data ->
        try
        {
            CgsGameInfoDisplayMenu(CgsGameType.valueOf(data)).openMenu(player)
        } catch (exception: Exception)
        {
            player.sendMessage("${net.evilblock.cubed.util.CC.RED}Sorry, all ${net.evilblock.cubed.util.CC.YELLOW}$data${net.evilblock.cubed.util.CC.RED} servers are offline.")
        }
    })
}
