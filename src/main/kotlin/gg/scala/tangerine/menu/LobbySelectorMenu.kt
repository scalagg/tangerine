package gg.scala.tangerine.menu

import com.cryptomorin.xseries.XMaterial
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.lemon.redirection.impl.VelocityRedirectSystem
import gg.scala.tangerine.configurable.ConfigurableMenuOutput
import gg.scala.tangerine.configurable.ConfigurableMenuTemplate
import gg.scala.tangerine.placeholder.PlaceholderHandler
import gg.scala.tangerine.tracking.TangerineLobbyTracker
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
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
) : PaginatedMenu()
{
    init
    {
        autoUpdate = true
        autoUpdateInterval = 500L

        placeholdBorders = true
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        return mutableMapOf<Int, Button>().also { map ->
            TangerineLobbyTracker.lobbies
                .entries
                .sortedByDescending { it.key }
                .forEach {
                    map[map.size] = LobbyButton(it.value)
                }
        }
    }

    override fun size(buttons: Map<Int, Button>) = 36
    override fun getMaxItemsPerPage(player: Player) = 14

    override fun getAllPagesButtonSlots() = (10..16) + (19..25)

    override fun getPrePaginatedTitle(player: Player) = template.title

    private inner class LobbyButton(
        private val serverInstance: GameServer
    ) : Button()
    {
        override fun getMaterial(player: Player): XMaterial
        {
            return XMaterial.matchXMaterial(
                template.buttons[0]!!.itemStack.type
            )
        }

        override fun getName(player: Player): String
        {
            return template.buttons[0]!!.itemStack.itemMeta
                .displayName.replace("<hub>", serverInstance.id)
        }

        override fun getDescription(player: Player): List<String>
        {
            val originalLore = template.buttons[0]!!.itemStack.itemMeta.lore
            val replacedLore = mutableListOf<String>()

            originalLore.forEach {
                replacedLore.add(
                    it.replace("<hub>", serverInstance.id)
                )
            }

            return PlaceholderHandler.findProvider().handle(player, replacedLore)
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView)
        {
            VelocityRedirectSystem.redirect(player, serverInstance.id)
        }
    }
}
