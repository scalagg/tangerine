package gg.scala.tangerine.listener

import gg.scala.tangerine.TangerineSpigotPlugin
import io.papermc.lib.PaperLib
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.Sound
import org.bukkit.block.Skull
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EnderPearl
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
import org.spigotmc.event.entity.EntityDismountEvent

/**
 * @author GrowlyX
 * @since 9/16/2021
 */
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
            val state = event.clickedBlock.state

            if (state is Skull)
            {
                if (state.skullType !== SkullType.PLAYER)
                {
                    return
                }

                state.owner?.let {
                    event.player.sendMessage("${CC.YELLOW}This is the head of: ${CC.WHITE}$it")
                }
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

    private val pearl = ItemBuilder(Material.ENDER_PEARL).name(CC.B_PRI + "Enderpearl").build()

    @EventHandler
    fun onProjectileLaunchEvent(event: ProjectileLaunchEvent)
    {
        if (event.entity is EnderPearl && event.entity.shooter is Player)
        {
            val player = event.entity.shooter as Player

            if (player.vehicle != null)
            {
                val vehicle = player.vehicle
                player.eject()
                vehicle.remove()
            }

            event.entity.velocity = player.location.direction.normalize().multiply(1.5F)
            event.entity.passenger = player

            player.world.playSound(player.location, Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F)

            Tasks.delayed(1L)
            {
                player.inventory.setItem(8, pearl)
                player.updateInventory()
            }
        }
    }

    @EventHandler
    fun onEntityDismountEvent(event: EntityDismountEvent)
    {
        if (event.entity is Player && event.entity.vehicle is EnderPearl)
        {
            val vehicle = event.entity.vehicle

            event.entity.eject()

            Tasks.delayed(1L)
            {
                if (!vehicle.isDead)
                {
                    vehicle.remove()
                }
            }
        }
    }
}
