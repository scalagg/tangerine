package gg.scala.tangerine.agflink

import gg.scala.lemon.server.ServerInstance
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView

/**
 * @author GrowlyX
 * @since 10/26/2021
 */
class AgfGameInfoDisplayMenu(
    private val agfGameType: AgfGameType
) : Menu("Servers ${Constants.DOUBLE_ARROW_RIGHT} ${agfGameType.formatted}")
{
    init
    {
        placeholdBorders = true
        autoUpdate = true
    }

    override fun size(buttons: Map<Int, Button>): Int
    {
        // TODO: 10/26/2021 change dynamically depending on amount of servers online
        return 27
    }

    override fun getButtons(player: Player): Map<Int, Button>
    {
        return hashMapOf<Int, Button>().also {
            val servers = AgfGameInfoUpdater.gameInfo[agfGameType]

            servers?.forEach { gameInfo ->
                if (it.size == 7)
                {
                    return@forEach
                }

                it[it.size + 10] = GameInfoButton(gameInfo)
            }
        }
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
        private val serverInstance: ServerInstance
    ) : Button()
    {
        private val gameState = AgfGameState.valueOf(
            serverInstance.metaData["game-state"]!!
        )

        override fun getMaterial(player: Player): Material
        {
            return Material.WOOL
        }

        override fun getDamageValue(player: Player): Byte
        {
            return when (gameState)
            {
                AgfGameState.WAITING -> 5
                AgfGameState.IN_GAME -> 13
                AgfGameState.STARTING -> 4
                AgfGameState.ENDING -> 1
            }
        }

        override fun getName(player: Player): String
        {
            return "${CC.B_PRI}${serverInstance.serverId}"
        }

        override fun getDescription(player: Player): List<String>
        {
            return mutableListOf(
                "${CC.GRAY}Players: ${CC.WHITE}${
                    serverInstance.onlinePlayers
                }/${serverInstance.maxPlayers}",
                "",
                getStatus(),
                "",
            ).also {
                it.addAll(getInability())
            }
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType, view: InventoryView)
        {
            player.chat("/join ${serverInstance.serverId}")
        }

        private fun getInability() = if (serverInstance.whitelisted)
        {
            listOf("${CC.RED}You cannot join a", "${CC.RED}whitelisted server.")
        } else
        {
            listOf("${CC.YELLOW}Click to connect.")
        }

        private fun getStatus() = when (gameState)
        {
            AgfGameState.WAITING -> "${CC.YELLOW}Waiting for players..."
            AgfGameState.IN_GAME -> "${CC.D_GREEN}Game running..."
            AgfGameState.STARTING -> "${CC.GREEN}Game starting..."
            AgfGameState.ENDING -> "${CC.GOLD}Booting up..."
        }
    }
}
