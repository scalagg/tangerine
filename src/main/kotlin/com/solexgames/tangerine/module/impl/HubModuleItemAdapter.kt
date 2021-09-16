package com.solexgames.tangerine.module.impl

import me.lucko.helper.Events
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 8/5/2021
 */
interface HubModuleItemAdapter {

    val additionalItemsAndClickEvents: Map<ItemStack, Map.Entry<Int, (Player) -> Unit>>

    fun handle(player: Player) {
        additionalItemsAndClickEvents.forEach {
            player.inventory.setItem(it.value.key, it.key)
        }
    }

    fun init() {
        additionalItemsAndClickEvents.forEach { entry ->
            Events.subscribe(PlayerInteractEvent::class.java)
                .filter { it.action.name.contains("RIGHT") }
                .filter { it.item.isSimilar(entry.key) }
                .handler { entry.value.value.invoke(it.player) }
        }
    }
}
