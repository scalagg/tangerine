package com.solexgames.tangerine.menu

import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
enum class ConfigurableMenuButtonType(
    val handle: (Player, String) -> Unit
) {
    NONE({ player, data ->

    }),
    COMMAND({ player, data ->

    }),
    QUEUE({ player, data ->

    }),
    SEND_SERVER({ player, data ->

    })
}
