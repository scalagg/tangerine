package gg.scala.tangerine.updater

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.tangerine.placeholder.PlaceholderHandler
import me.lucko.helper.Schedulers
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity

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
        Schedulers.async().runRepeating(
            this, 0L, 10L
        )
    }

    override fun run()
    {
        EntityHandler.getEntitiesByType(NpcEntity::class.java).forEach {
            it.hologram.updateLines(
                PlaceholderHandler.findProvider()?.handle(null, it.getLines())
                    ?: it.getLines()
            )
        }
    }
}
