package gg.scala.tangerine.configurable.editor

import com.cryptomorin.xseries.XMaterial
import gg.scala.lemon.player.metadata.Metadata
import gg.scala.tangerine.configurable.ConfigurableMenuButtonData
import gg.scala.tangerine.configurable.ConfigurableMenuButtonType
import gg.scala.tangerine.configurable.ConfigurableMenuHandler
import gg.scala.tangerine.configurable.ConfigurableMenuTemplate
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.Color
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks.delayed
import net.evilblock.cubed.util.bukkit.prompt.InputPrompt
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 9/18/2021
 */
class ConfigurableMenuEditorDetailedButtonView(
    private val data: ConfigurableMenuButtonData,
    private val template: ConfigurableMenuTemplate
) : Menu() {

    init {
        updateAfterClick = true
    }

    override fun getTitle(player: Player): String {
        return "Menu Editor ${Constants.DOUBLE_ARROW_RIGHT} Editing Button"
    }

    override fun getButtons(player: Player): Map<Int, Button> {
        return hashMapOf<Int, Button>().also {
//            it[it.size] = MultiOptionPlayerSettingsBuilder()
//                .titleOf("Action")
//                .descriptionOf(
//                    "What action would you",
//                    "like the button to issue",
//                    "on click?"
//                )
//                .materialOf(XMaterial.COMPASS)
//                .orderedValuesOf(
//                    *ConfigurableMenuButtonType.values()
//                        .map { value -> value.name }.toTypedArray()
//                )
//                .fallbackOf("NONE")
//                .valueOverrideOf { value ->
//                    data.menuButtonType = ConfigurableMenuButtonType.valueOf(value)
//
//                    ConfigurableMenuHandler.saveData()
//                }
//                .providerOverrideOf { _, _ ->
//                    Metadata(data.menuButtonType.name)
//                }
//                .settingOf("n/a").asButton()

            it[0] = ItemBuilder(XMaterial.OAK_SIGN)
                .name("${CC.GREEN}Action Data")
                .addToLore("${CC.GRAY}Set the action type.", "", "${CC.YELLOW}Click to modify.")
                .toButton { _, _ ->
                    InputPrompt()
                        .withText("${CC.SEC}Enter ${CC.PRI}Action Data${CC.SEC} (Type \"cancel\" to exit)")
                        .acceptInput { player, input ->
                            if (input.equals("cancel", true)) {
                                player.sendMessage("${CC.SEC}Modification cancelled.")
                                return@acceptInput
                            }
                            player.sendMessage("${CC.SEC}Set ${CC.PRI}Action Data${CC.SEC} to ${CC.WHITE}$input${CC.SEC}.")
                            data.menuButtonTypeData = input
                            ConfigurableMenuHandler.saveData()
                            openMenu(player)
                        }
                        .start(player)
                }

            it[1] = ItemBuilder(XMaterial.NAME_TAG)
                .name("${CC.GREEN}Title Data")
                .addToLore(
                    "${CC.GRAY}Edit the button title.",
                    "",
                    "${CC.SEC}Current Title: ${CC.WHITE}${data.itemStack.itemMeta.displayName}",
                    "",
                    "${CC.YELLOW}Click to modify.")
                .toButton { _, _ ->
                    InputPrompt()
                        .withText("${CC.SEC}Enter new button title (Type \"cancel\" to exit)")
                        .acceptInput { player, input ->
                            if (!input.equals("cancel", true)) {
                                data.itemStack.itemMeta.displayName = Color.translate(input)
                                ConfigurableMenuHandler.saveData()
                            }
                            openMenu(player)
                        }
                        .start(player)
                }

            it[2] = ItemBuilder(data.itemStack.type)
                .name("${CC.GREEN}Display Icon")
                .addToLore(
                    "${CC.GRAY}Modify the button icon.",
                    "",
                    "${CC.SEC}Current Icon: ${CC.WHITE}${data.itemStack.type.name}",
                    "",
                    "${CC.YELLOW}Click to modify.")
                .toButton { _, _ ->
                    InputPrompt()
                        .withText("${CC.SEC}Enter new material type (Type \"cancel\" to exit)")
                        .acceptInput { player, input ->
                            val material = XMaterial.matchXMaterial(input.uppercase()).orElse(null)
                            if (material != null) {
                                data.itemStack.type = material.parseMaterial() ?: data.itemStack.type
                                ConfigurableMenuHandler.saveData()
                            } else {
                                player.sendMessage("${CC.RED}Invalid icon type.")
                            }
                            openMenu(player)
                        }
                        .start(player)
                }

            it[3] = ItemBuilder(XMaterial.WRITABLE_BOOK)
                .name("${CC.GREEN}Description Data")
                .addToLore("${CC.GRAY}Edit the button description.", "", "${CC.YELLOW}Click to modify.")
                .toButton { _, _ ->
                    ConfigurableMenuDescriptionEditor(data, data.itemStack.itemMeta.lore).openMenu(player)
                }
        }
    }

    override fun onClose(player: Player, manualClose: Boolean) {
        if (manualClose) {
            delayed(1L) {
                ConfigurableMenuEditorDetailedView(template).openMenu(player)
            }
        }
    }
}
