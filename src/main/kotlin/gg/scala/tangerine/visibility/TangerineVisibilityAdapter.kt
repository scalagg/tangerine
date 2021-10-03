package gg.scala.tangerine.visibility

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

        if (!toRefresh.hasPermission("tangerine.visibility-override")) {
            visibilityAction = VisibilityAction.HIDE
        } else if (refreshFor.hasMetadata("hiding-players")) {
            visibilityAction = VisibilityAction.HIDE
        }

        return visibilityAction
    }
}
