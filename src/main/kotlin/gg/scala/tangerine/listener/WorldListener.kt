package gg.scala.tangerine.listener

import gg.scala.commons.annotations.Listeners
import gg.scala.lemon.Lemon
import gg.scala.lemon.metadata.NetworkMetadataDataSync
import gg.scala.tangerine.TangerineSpigotPlugin
import io.papermc.lib.PaperLib
import me.lucko.helper.event.filter.EventFilters
import net.evilblock.cubed.util.CC
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.block.Skull
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.painting.PaintingBreakEvent
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
    fun onPlayerInventory(event: InventoryClickEvent)
    {
        if (NetworkMetadataDataSync.serverName() == "Minequest")
        {
            if (
                event.currentItem == null ||
                event.currentItem.type == Material.AIR
            )
            {
                event.isCancelled = false
                return
            }

            val clickedOnCosmetic = event.currentItem
                ?.itemMeta?.lore?.any {
                    "Right-click to equip" in it || "You can't equip this item!" in it
                }
                ?: false

            if (clickedOnCosmetic)
            {
                event.isCancelled = false
                return
            }
        }

        if (!event.whoClicked.hasMetadata("builder"))
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun PlayerMoveEvent.on()
    {
        if (
            !EventFilters
                .ignoreSameBlockAndY<PlayerMoveEvent>()
                .test(this)
        )
        {
            return
        }

        val block = to.block.getRelative(BlockFace.DOWN)
        if (block.type == Material.SPONGE)
        {
            val vector = player.location.direction
                .multiply(3.25)
                .setY(1.05)

            player.playSound(player.location, Sound.GHAST_FIREBALL, 1.0f, 1.0f)
            player.velocity = vector
        }
    }

    @EventHandler()
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
        if (
            !event.player.hasMetadata("builder") &&
            (event.action == Action.RIGHT_CLICK_BLOCK ||
                event.action == Action.LEFT_CLICK_BLOCK)
        )
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent)
    {
        event.deathMessage = null
        event.keepInventory = true
        event.keepLevel = true

        PaperLib.teleportAsync(
            event.entity,
            TangerineSpigotPlugin.instance.settings.spawnLocation!!
        )
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent)
    {
        event.isCancelled = true

        if (event.entity is Player)
        {
            val player = event.entity as Player

            if (
                event.cause == EntityDamageEvent.DamageCause.VOID &&
                TangerineSpigotPlugin.instance.settings.spawnLocation != null
            )
            {
                PaperLib.teleportAsync(player, TangerineSpigotPlugin.instance.settings.spawnLocation!!)
            }

            if (
                event.cause == EntityDamageEvent.DamageCause.DROWNING &&
                NetworkMetadataDataSync.serverName() == "Minequest"
            )
            {
                event.isCancelled = false
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
        if (
            event.entityType == EntityType.ARMOR_STAND ||
            event.entityType == EntityType.SKELETON ||
            event.entityType == EntityType.ARROW ||
            event.entityType == EntityType.DROPPED_ITEM
        )
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

        if (
            event.entityType == EntityType.SKELETON ||
            event.entityType == EntityType.ARROW
        )
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
}
