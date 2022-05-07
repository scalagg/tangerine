package gg.scala.tangerine.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.lemon.server.ServerInstance
import gg.scala.tangerine.configurable.ConfigurableMenuOutput
import gg.scala.tangerine.configurable.ConfigurableMenuTemplate
import gg.scala.tangerine.placeholder.PlaceholderHandler
import gg.scala.tangerine.tracking.TangerineLobbyTracker
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.bungee.BungeeUtil
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

/**
 * @author GrowlyX
 * @since 9/24/2021
 */
class LobbySelectorMenu(
    private val template: ConfigurableMenuTemplate
) : ConfigurableMenuOutput(template.id) {

    override fun getButtons(player: Player): Map<Int, Button> {
        return mutableMapOf<Int, Button>().also { map ->
            TangerineLobbyTracker.lobbies.forEach {
                if (map.size >= 7) {
                    return@forEach
                }

                map[9 + it.key] = LobbyButton(it.value)
            }
        }
    }

    private inner class LobbyButton(
        private val serverInstance: ServerInstance
    ) : Button() {

        override fun getMaterial(player: Player): XMaterial {
            return XMaterial.matchXMaterial(
                template.buttons[0]!!.itemStack.type
            )
        }

        override fun getName(player: Player): String {
            return template.buttons[0]!!.itemStack.itemMeta
                .displayName.replace("<hub>", serverInstance.serverId)
        }

        override fun getDescription(player: Player): List<String> {
            val originalLore = template.buttons[0]!!.itemStack.itemMeta.lore
            val replacedLore = mutableListOf<String>()

            originalLore.forEach {
                replacedLore.add(
                    it.replace("<hub>", serverInstance.serverId)
                )
            }

            return PlaceholderHandler.findProvider().handle(player, replacedLore)
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView) {
            BungeeUtil.sendToServer(player, serverInstance.serverId)
        }
    }
}
