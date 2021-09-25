package gg.scala.tangerine.visibility

import net.evilblock.cubed.visibility.OverrideAction
import net.evilblock.cubed.visibility.OverrideHandler
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
object TangerineVisibilityOverride : OverrideHandler {

    override fun getAction(toRefresh: Player, refreshFor: Player): OverrideAction {
        return if (toRefresh.hasPermission("tangerine.visibility-override")) OverrideAction.SHOW else OverrideAction.NEUTRAL
    }
}
