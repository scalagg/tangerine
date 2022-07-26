package gg.scala.tangerine.minequest

import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.terminable.composite.CompositeTerminable
import net.evilblock.cubed.util.bukkit.cuboid.Cuboid
import org.apache.commons.lang3.tuple.MutableTriple
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.util.Vector
import java.util.*

/**
 * @author GrowlyX
 * @since 7/26/2022
 */
class MinequestPortal
@JvmOverloads
constructor(
    private val cuboid: Cuboid,
    private val height: Double = 1.777,
    private val limit: Double = 0.10,
    private val restrictBelowAir: Boolean = true,
    private val portalEnter: (Player) -> Unit = {},
    private val portalExit: (Player) -> Unit = {},
    private val portalTeleport: (Player) -> Unit = {},
)
{
    private val floating =
        mutableMapOf<UUID, MutableTriple<Location, Int, CompositeTerminable>>()

    fun subscribe(terminable: CompositeTerminable)
    {
        Events.subscribe(PlayerQuitEvent::class.java)
            .handler {
                this.floating.remove(it.player.uniqueId)
                    ?.right?.closeAndReportException()
            }

        Events.subscribe(PlayerMoveEvent::class.java)
            .handler {
                val player = it.player

                val withinCuboid = this.cuboid
                    .contains(player)

                if (
                    !withinCuboid && this.floating
                        .contains(player.uniqueId)
                )
                {
                    this.floating.remove(player.uniqueId)
                        ?.right?.closeAndReportException()

                    this.portalExit.invoke(player)

                    player.isFlying = false
                    player.allowFlight = false
                    return@handler
                }

                val location = player.location

                if (
                    withinCuboid && !this.floating.contains(player.uniqueId)
                )
                {
                    if (restrictBelowAir)
                    {
                        val block = location.block.location.clone()
                            .add(0.0, -1.0, 0.0).block

                        if (block.type != Material.AIR)
                        {
                            return@handler
                        }
                    }

                    this.floating.putIfAbsent(
                        player.uniqueId,
                        MutableTriple(
                            location.clone(), 1,
                            CompositeTerminable.create()
                        )
                    )

                    val floaterInfo = this
                        .floating[player.uniqueId]!!

                    this.portalEnter.invoke(player)

                    Schedulers.sync()
                        .runRepeating({ task ->
                            if (!player.isOnline)
                            {
                                this.floating.remove(player.uniqueId)
                                task.closeAndReportException()
                                return@runRepeating
                            }

                            if (floaterInfo.middle <= 6)
                            {
                                player.velocity = Vector(
                                    player.velocity.x + .03,
                                    player.velocity.y * .2,
                                    player.velocity.z
                                )

                                player.velocity = player.location.direction
                                    .multiply(0.03).setY(0.01)

                                if (floaterInfo.middle == 6)
                                {
                                    player.isFlying = false
                                    task.closeAndReportException()
                                    return@runRepeating
                                }

                                floaterInfo.setMiddle(
                                    floaterInfo.middle + 1
                                )
                            }
                        }, 0L, 1L)
                        .bindWith(floaterInfo.right)

                    var increment = 0.0

                    Schedulers.sync()
                        .runRepeating({ task ->
                            if (!player.isOnline)
                            {
                                this.floating.remove(player.uniqueId)
                                task.closeAndReportException()
                                return@runRepeating
                            }

                            val currentLocation = player.location

                            if (currentLocation.y >= location.y + height)
                            {
                                this.portalTeleport.invoke(player)
                                floaterInfo.right.closeAndReportException()
                                return@runRepeating
                            }

                            if (player.gameMode != GameMode.CREATIVE)
                            {
                                player.velocity = currentLocation
                                    .direction.multiply(0.03)
                                    .setY(.03 + increment)

                                if (increment <= limit)
                                {
                                    increment += .01
                                }
                            }
                        }, 6L, 1L)
                        .bindWith(floaterInfo.right)
                }
            }
            .bindWith(terminable)
    }
}
