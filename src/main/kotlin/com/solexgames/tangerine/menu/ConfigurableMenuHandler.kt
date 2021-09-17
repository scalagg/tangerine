package com.solexgames.tangerine.menu

import com.cryptomorin.xseries.XMaterial
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player
import java.io.File
import java.lang.reflect.Type

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
object ConfigurableMenuHandler {

    private val dataType: Type = object : TypeToken<MutableCollection<ConfigurableMenuTemplate>>() {}.type

    val templates = mutableMapOf<String, ConfigurableMenuTemplate>()

    private fun getDataFile(): File {
        return File("Tangerine", "menu-configuration.json")
    }

    fun initialLoad() {
        if (getDataFile().exists()) {
            val reader = Files.newReader(getDataFile(), Charsets.UTF_8)

            if (reader != null) {
                val collection = Serializers.gson.fromJson<MutableCollection<ConfigurableMenuTemplate>>(reader, dataType)

                if (collection != null) {
                    collection.forEach { templates[it.id] = it }
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
        templates["server-selector"] = ConfigurableMenuTemplate("server-selector").also {
            it.buttons[13] = ConfigurableMenuButtonData(
                ItemBuilder(XMaterial.INK_SAC)
                    .name("This is an example item stack")
                    .amount(4)
                    .data(1)
                    .addToLore(
                        "Example lore"
                    ).build(),
                ConfigurableMenuButtonType.COMMAND,
                "time day"
            )
        }

        templates["hub-selector"] = ConfigurableMenuTemplate("server-selector").also {
            it.buttons[13] = ConfigurableMenuButtonData(
                ItemBuilder(XMaterial.INK_SAC)
                    .name("This is an example item stack")
                    .amount(4)
                    .data(1)
                    .addToLore(
                        "Example lore"
                    ).build(),
                ConfigurableMenuButtonType.COMMAND,
                "time day"
            )
        }

        saveData()
    }

    fun saveData() {
        Files.write(Serializers.gson.toJson(templates.values, dataType), getDataFile(), Charsets.UTF_8)
    }

    @JvmStatic
    fun openMenu(player: Player, id: String) {
        val template = templates[id]

        if (template != null) {
            ConfigurableMenuOutput(template).openMenu(player)
        }
    }
}
