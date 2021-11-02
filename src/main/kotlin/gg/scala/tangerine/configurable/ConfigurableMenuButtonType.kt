package gg.scala.tangerine.configurable

import gg.scala.tangerine.agflink.AgfGameInfoDisplayMenu
import gg.scala.tangerine.agflink.AgfGameType
import net.evilblock.cubed.util.bungee.BungeeUtil
import org.bukkit.Bukkit
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
        Bukkit.dispatchCommand(player, data)
    }),
    QUEUE({ player, data ->
        Bukkit.dispatchCommand(player, "joinqueue $data")
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
            AgfGameInfoDisplayMenu(AgfGameType.valueOf(data)).openMenu(player)
        } catch (exception: Exception)
        {
            player.sendMessage("${net.evilblock.cubed.util.CC.RED}Sorry, all ${net.evilblock.cubed.util.CC.YELLOW}$data${net.evilblock.cubed.util.CC.RED} servers are offline.")
        }
    })
}
