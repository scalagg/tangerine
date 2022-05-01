package gg.scala.tangerine.menu

import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.settings.TangerineSettings
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 5/1/2022
 */
class JoinMessageEditMenu(
    lines: List<String>
) : TextEditorMenu(lines)
{
    override fun getPrePaginatedTitle(player: Player) =
        "Edit Join Message"

    override fun onClose(player: Player)
    {
        player.sendMessage("${CC.RED}Your changes were not saved.")
    }

    override fun onSave(player: Player, list: List<String>)
    {
        for (message in list)
        {
            player.sendMessage(
                Color.translate(message)
            )
        }

        val settings = TangerineSpigotPlugin
            .instance.settings

        settings.joinMessage.clear()
        settings.joinMessage.addAll(
            list.map { Color.translate(it) }
        )

        TangerineSpigotPlugin.instance
            .configContainerized
            .container<TangerineSettings>().save()

        player.sendMessage("${CC.D_GREEN}${Constants.CHECK_SYMBOL}${CC.GREEN} Saved & synced join message!")
    }
}
