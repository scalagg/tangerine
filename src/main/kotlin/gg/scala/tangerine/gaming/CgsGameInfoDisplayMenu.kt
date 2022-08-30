package gg.scala.tangerine.gaming

import com.cryptomorin.xseries.XMaterial
import gg.scala.commons.agnostic.sync.server.impl.GameServer
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

/**
 * @author GrowlyX
 * @since 10/26/2021
 */
class CgsGameInfoDisplayMenu(
    private val cgsGameType: CgsGameType
) : Menu("Servers ${Constants.DOUBLE_ARROW_RIGHT} ${cgsGameType.formatted}")
{
    init
    {
        placeholdBorders = true
        autoUpdate = true
    }

    override fun size(buttons: Map<Int, Button>): Int
    {
        return 54
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()

        val servers = CgsGameInfoUpdater
            .gameInfo[cgsGameType] ?: listOf()

        for ((index, region) in listOf("na", "eu", "as", "as").withIndex())
        {
            buttons[(9 * (index + 1))] = ItemBuilder
                .of(Material.STAINED_GLASS_PANE)
                .data((5 - index).toShort())
                .name("${CC.B_YELLOW}${region.uppercase()} ${cgsGameType.formatted}")
                .toButton()

            buttons[(17 + (index * 9))] = ItemBuilder
                .of(Material.AIR)
                .toButton()

            val filtered = servers
                .filter {
                    it.groups.first().endsWith("-${region}")
                }

            for ((serverIndex, serverInfo) in filtered.withIndex())
            {
                buttons[serverIndex + (10 + (index * 9))] =
                    GameInfoButton(serverInfo)
            }
        }

        return buttons
    }


    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.delayed(1L)
            {
                ConfigurableMenuHandler.openMenu(player, "server-selector")
            }
        }
    }

    inner class GameInfoButton(
        private val serverInstance: GameServer
    ) : Button()
    {
        private val gameState = CgsGameState.valueOf(
            serverInstance.getMetadataValue<String>("game", "game-state") ?: "ENDED"
        )

        override fun getMaterial(player: Player): XMaterial
        {
            return XMaterial.matchXMaterial(Material.WOOL)
        }

        override fun getDamageValue(player: Player): Byte
        {
            return when (gameState)
            {
                CgsGameState.WAITING -> 5
                CgsGameState.STARTED -> 13
                CgsGameState.STARTING -> 4
                CgsGameState.ENDED -> 1
            }
        }

        override fun getName(player: Player): String
        {
            return "${CC.B_PRI}${serverInstance.id}"
        }

        override fun getDescription(player: Player): List<String>
        {
            return mutableListOf(
                "${CC.GRAY}Players: ${CC.WHITE}${
                    serverInstance.getPlayersCount()
                }/${serverInstance.getMaxPlayers()}",
                "",
                getStatus(),
                "",
            ).also {
                it.addAll(getInability())
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView)
        {
            player.performCommand("join ${serverInstance.id}")
        }

        private fun getInability() = if (serverInstance.getWhitelisted() == true)
        {
            listOf("${CC.RED}You cannot join a", "${CC.RED}whitelisted server.")
        } else
        {
            listOf("${CC.YELLOW}Click to connect.")
        }

        private fun getStatus() = when (gameState)
        {
            CgsGameState.WAITING -> "${CC.YELLOW}Waiting for players..."
            CgsGameState.STARTED -> "${CC.D_GREEN}Game running..."
            CgsGameState.STARTING -> "${CC.GREEN}Game starting..."
            CgsGameState.ENDED -> "${CC.GOLD}Booting up..."
        }
    }
}
