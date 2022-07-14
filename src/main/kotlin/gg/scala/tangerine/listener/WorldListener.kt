package gg.scala.tangerine.listener

import gg.scala.commons.annotations.Listeners
import gg.scala.tangerine.TangerineSpigotPlugin
import io.papermc.lib.PaperLib
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.block.Skull
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.event.weather.ThunderChangeEvent
import org.bukkit.event.weather.WeatherChangeEvent

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
@Listeners
object WorldListener : Listener
{
    @EventHandler
    fun onDropItem(event: PlayerDropItemEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerInventory(event: InventoryClickEvent)
    {
        if (!event.whoClicked.hasMetadata("builder"))
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent)
    {
        if (!event.player.hasMetadata("builder"))
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent)
    {
        val block = event.clickedBlock

        if (block != null && block.type == Material.DRAGON_EGG)
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent)
    {
        event.isCancelled = true

        if (event.entity is Player)
        {
            val player = event.entity as Player

            if (event.cause == EntityDamageEvent.DamageCause.VOID && TangerineSpigotPlugin.instance.settings.spawnLocation != null)
            {
                PaperLib.teleportAsync(player, TangerineSpigotPlugin.instance.settings.spawnLocation!!)
            }
        }
    }

    @EventHandler
    fun onBlockInteract(event: PlayerInteractEvent)
    {
        if (event.action == Action.RIGHT_CLICK_BLOCK)
        {
            if (
                event.clickedBlock.type.name
                    .lowercase().contains("door")
            )
            {
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onEntityDamageByBlock(event: EntityDamageByBlockEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent)
    {
        if (!event.player.hasMetadata("builder"))
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onFeed(event: FoodLevelChangeEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent)
    {
        if (event.toWeatherState())
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onThunderChange(event: ThunderChangeEvent)
    {
        if (event.toThunderState())
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent)
    {
        if (event.entityType == EntityType.ARMOR_STAND)
        {
            event.isCancelled = false
            return
        }
        event.isCancelled = true
    }

    @EventHandler
    fun onMobSpawn(event: CreatureSpawnEvent)
    {
        if (event.entity is ArmorStand)
        {
            event.isCancelled = false
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun onBed(event: PlayerBedEnterEvent)
    {
        event.isCancelled = true
    }

    @EventHandler
    fun onItemPickup(event: PlayerPickupItemEvent)
    {
        if (!event.player.hasMetadata("builder"))
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBucketFill(event: PlayerBucketFillEvent)
    {
        if (!event.player.hasMetadata("builder"))
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBucketEmpty(event: PlayerBucketEmptyEvent)
    {
        if (!event.player.hasMetadata("builder"))
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent)
    {
        if (!event.player.hasMetadata("builder"))
        {
            event.isCancelled = true
        }
    }
}
