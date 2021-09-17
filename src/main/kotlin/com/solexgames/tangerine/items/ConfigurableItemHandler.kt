package com.solexgames.tangerine.items

import com.cryptomorin.xseries.XMaterial
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.inventory.ItemStack
import java.io.File
import java.lang.reflect.Type

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
object ConfigurableItemHandler {

    private val dataType: Type = object : TypeToken<MutableMap<Int, ItemStack?>>() {}.type

    var items = mutableMapOf<Int, ItemStack?>()

    private fun getDataFile(): File {
        return File("Tangerine", "item-configuration.json")
    }

    fun initialLoad() {
        if (getDataFile().exists()) {
            val reader = Files.newReader(getDataFile(), Charsets.UTF_8)

            if (reader != null) {
                val collection = Serializers.gson.fromJson<MutableMap<Int, ItemStack?>>(reader, dataType)

                if (collection != null) {
                    items = collection
                } else {
                    setupIfEmpty()
                }
            } else {
                setupIfEmpty()
            }
        } else {
            getDataFile().mkdirs()

            setupIfEmpty()
        }
    }

    private fun setupIfEmpty() {
        items[3] = ItemBuilder(XMaterial.COMPASS)
            .name("${CC.B_PRI}Server Selector")
            .addToLore(
                "${CC.GRAY}View all our servers."
            )
            .build()
        items[5] = ItemBuilder(XMaterial.NETHER_STAR)
            .name("${CC.B_PRI}Hub Selector")
            .addToLore(
                "${CC.GRAY}View all our lobbies."
            )
            .build()

        saveData()
    }

    fun saveData() {
        Files.write(Serializers.gson.toJson(items, dataType), getDataFile(), Charsets.UTF_8)
    }
}
