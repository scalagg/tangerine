package gg.scala.tangerine.configurable

import com.cryptomorin.xseries.XMaterial
import com.google.common.base.Charsets
import com.google.common.io.Files
import com.google.gson.reflect.TypeToken
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.tangerine.TangerineSpigotPlugin
import net.evilblock.cubed.serializers.Serializers
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.entity.Player
import java.io.File
import java.lang.reflect.Type

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
@Service
object ConfigurableMenuHandler {

    private val dataType: Type = object : TypeToken<MutableCollection<ConfigurableMenuTemplate>>() {}.type

    val templates = mutableMapOf<String, ConfigurableMenuTemplate>()

    private fun getDataFile(): File {
        return File(TangerineSpigotPlugin.instance.dataFolder, "menu-configuration.json")
    }

    @Configure
    fun configure() {
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
            getDataFile().createNewFile()

            setupIfEmpty()
        }
    }

    fun reloadMenus() {
        templates.clear()
        configure()
    }

    private fun setupIfEmpty() {
        templates["server-selector"] = ConfigurableMenuTemplate("server-selector").also {
            it.id = "Server Selector"

            it.buttons[13] = ConfigurableMenuButtonData(
                ItemBuilder(XMaterial.INK_SAC)
                    .name("This is an example item stack")
                    .amount(4)
                    .data(1)
                    .addToLore(
                        "Example lore"
                    ).build(),
                ConfigurableMenuButtonType.MENU,
                "other-selector"
            )
        }

        templates["other-selector"] = ConfigurableMenuTemplate("other-selector").also {
            it.id = "Secondary Selector"

            it.buttons[13] = ConfigurableMenuButtonData(
                ItemBuilder(XMaterial.INK_SAC)
                    .name("This is an example item stack")
                    .amount(4)
                    .data(1)
                    .addToLore(
                        "Example lore"
                    ).build(),
                ConfigurableMenuButtonType.MENU,
                "server-selector"
            )
        }

        templates["hub-selector"] = ConfigurableMenuTemplate("hub-selector").also {
            it.id = "Lobby Selector"

            it.buttons[0] = ConfigurableMenuButtonData(
                ItemBuilder(XMaterial.NETHER_STAR)
                    .name("${CC.B_PRI}<hub>")
                    .addToLore(
                        "%tangerine_status-<hub>%",
                        "${CC.GRAY}Online: ${CC.WHITE}%tangerine_players-<hub>%/%tangerine_max-<hub>%",
                        "",
                        "%tangerine_joinable-<hub>%"
                    )
                    .build(),
                ConfigurableMenuButtonType.NONE,
                ""
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
            try {
                ConfigurableMenuOutput(template).openMenu(player)
            } catch (exception: Exception) {
                player.sendMessage("${CC.RED}An error occurred while trying to open a menu, sorry for the inconvenience.")

                exception.printStackTrace()
            }
        }
    }
}
