package gg.scala.tangerine.updater

import gg.scala.tangerine.placeholder.PlaceholderHandler
import me.lucko.helper.Schedulers
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.entity.npc.NpcEntity

/**
 * @author GrowlyX
 * @since 10/25/2021
 */
object NpcTextUpdater : Runnable
{

    fun initialLoad()
    {
        Schedulers.async().runRepeating(
            this, 0L, 20L
        )
    }

    override fun run()
    {
        EntityHandler.getEntities().forEach {
            if (it is NpcEntity)
            {
                it.hologram.updateLines(
                    PlaceholderHandler.findProvider()?.handle(null, it.getLines())
                        ?: it.getLines()
                )
            }
        }
    }
}
