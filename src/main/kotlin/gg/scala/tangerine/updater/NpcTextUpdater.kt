package gg.scala.tangerine.updater

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.lemon.Lemon
import gg.scala.tangerine.placeholder.PlaceholderHandler
import me.lucko.helper.Schedulers
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity
import org.bukkit.Bukkit

/**
 * @author GrowlyX
 * @since 10/25/2021
 */
@Service
object NpcTextUpdater : Runnable
{
    @Configure
    fun configure()
    {
        if (Lemon.instance.settings.group == "hub")
        {
            Schedulers.async().runRepeating(
                this, 0L, 10L
            )
        }
    }

    fun getNpcEntities(): List<NpcEntity> =
        EntityHandler.getEntitiesByType(NpcEntity::class.java)

    override fun run()
    {
        val players = Bukkit
            .getOnlinePlayers()

        if (players.isEmpty())
            return

        EntityHandler.getEntitiesByType(NpcEntity::class.java)
            .forEach {
                if (it.condition != "hub") {
                    return@forEach
                }
                    it.hologram.updateLines(
                        PlaceholderHandler.findProvider()
                            .handle(players.first(), it.getLines())
                    )
            }
    }
}
