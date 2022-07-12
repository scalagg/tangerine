package gg.scala.tangerine.items2

import com.cryptomorin.xseries.XMaterial
import gg.scala.commons.config.convert.annotations.ConfigCloudSync
import gg.scala.commons.config.convert.annotations.ConfigConverted
import gg.scala.commons.config.convert.annotations.ConfigFileName
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder

/**
 * @author GrowlyX
 * @since 7/12/2022
 */
@ConfigCloudSync
@ConfigConverted("")
@ConfigFileName("items.json")
data class Items2Config(
    val items: List<Items2Model> = listOf(
        Items2Model(
            0,
            ItemBuilder(XMaterial.COMPASS)
                .name("${CC.B_PRI}Server Selector")
                .addToLore(
                    "${CC.GRAY}View all our servers."
                )
                .build(),
            Items2Action.Menu,
            listOf("server-selector")
        ),
        Items2Model(
            8,
            ItemBuilder(XMaterial.COMPASS)
                .name("${CC.B_PRI}Hub Selector")
                .addToLore(
                    "${CC.GRAY}View all our hub servers."
                )
                .build(),
            Items2Action.Menu,
            listOf("hub-selector")
        )
    )
)
