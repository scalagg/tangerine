package gg.scala.tangerine.items2

import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.menu.LobbySelectorMenu
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/12/2022
 */
enum class Items2Action(
    val lambda: (Player, List<String>) -> Unit = { _, _ -> }
)
{
    Menu(scope@{ player, meta ->
        val first = meta.first()

        if (first == "hub-selector")
        {
            val template = ConfigurableMenuHandler
                .templates[first]
                ?: return@scope player.sendMessage(
                    "${net.evilblock.cubed.util.CC.RED}We were unable to parse an internal template to open this menu."
                )

            LobbySelectorMenu(template).openMenu(player)
            return@scope
        }

        ConfigurableMenuHandler.openMenu(player, first)
    }),

    PlayerVisibility,
    Command(scope@{ player, meta ->
        meta.forEach {
            player.chat("/$it")
        }
    })
}
