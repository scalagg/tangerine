package gg.scala.tangerine.minequest

import net.evilblock.cubed.visibility.VisibilityAction
import net.evilblock.cubed.visibility.VisibilityAdapter
import net.evilblock.cubed.visibility.VisibilityAdapterRegister
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 2/11/2023
 */
@VisibilityAdapterRegister("minequestportals")
object MinequestPortalVisibilityAdapter : VisibilityAdapter
{
    override fun getAction(toRefresh: Player, refreshFor: Player): VisibilityAction
    {
        if (MinequestPortals.isEmpty())
        {
            return VisibilityAction.NEUTRAL
        }

        val portal = MinequestPortals
            .any {
                it.floating[toRefresh.uniqueId] != null
            }

        if (portal)
        {
            return VisibilityAction.HIDE
        }

        return VisibilityAction.NEUTRAL
    }
}
