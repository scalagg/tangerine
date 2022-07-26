package gg.scala.tangerine.visibility

import gg.scala.tangerine.minequest.MinequestPortals
import net.evilblock.cubed.visibility.VisibilityAction
import net.evilblock.cubed.visibility.VisibilityAdapter
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
object TangerineVisibilityAdapter : VisibilityAdapter {

    override fun getAction(toRefresh: Player, refreshFor: Player): VisibilityAction {
        var visibilityAction = VisibilityAction.NEUTRAL

        val portal = MinequestPortals
            .any {
                it.floating.containsKey(toRefresh.uniqueId)
            }

        if (!toRefresh.hasPermission("tangerine.visibility-override")) {
            visibilityAction = VisibilityAction.HIDE
        } else if (refreshFor.hasMetadata("hiding-players")) {
            visibilityAction = VisibilityAction.HIDE
        } else if (portal)
        {
            visibilityAction = VisibilityAction.HIDE
        }

        return visibilityAction
    }
}
