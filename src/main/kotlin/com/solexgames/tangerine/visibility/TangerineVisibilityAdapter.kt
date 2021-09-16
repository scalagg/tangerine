package com.solexgames.tangerine.visibility

import net.evilblock.cubed.visibility.VisibilityAction
import net.evilblock.cubed.visibility.VisibilityAdapter
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
object TangerineVisibilityAdapter : VisibilityAdapter {

    override fun getAction(toRefresh: Player, refreshFor: Player): VisibilityAction {
        return VisibilityAction.HIDE
    }
}
