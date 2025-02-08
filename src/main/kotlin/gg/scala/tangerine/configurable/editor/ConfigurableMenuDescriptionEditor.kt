package gg.scala.tangerine.configurable.editor

import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.configurable.ConfigurableMenuButtonData
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.settings.TangerineSettings
import net.evilblock.cubed.menu.menus.TextEditorMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.entity.Player

/**
 * @author Topu
 * @date 2/3/2025
 */
class ConfigurableMenuDescriptionEditor(
    private val data: ConfigurableMenuButtonData,
    lines: List<String>
) : TextEditorMenu(lines)
{
    override fun getPrePaginatedTitle(player: Player) =
        "Edit Button Description"

    override fun onClose(player: Player) {}

    override fun onSave(player: Player, list: List<String>)
    {
        for (message in list)
        {
            player.sendMessage(
                Color.translate(message)
            )
        }

        data.itemStack.itemMeta.lore.clear()
        data.itemStack.itemMeta.lore.addAll(
            list.map { Color.translate(it) }
        )

        ConfigurableMenuHandler.saveData()

        player.sendMessage("${CC.D_GREEN}${Constants.CHECK_SYMBOL}${CC.GREEN} Saved & synced button description!")
    }
}
