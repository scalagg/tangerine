package com.solexgames.tangerine.menu

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
    QUEUE({ player, _ ->
        player.sendMessage("${net.evilblock.cubed.util.CC.RED}Not available.")
    }),
    SEND_SERVER({ player, data ->
        BungeeUtil.sendToServer(player, data)
    }),
    MENU({ player, data ->
        ConfigurableMenuHandler.openMenu(player, data)
    })
}
