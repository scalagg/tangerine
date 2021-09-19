package gg.scala.tangerine.menu.editor

import com.cryptomorin.xseries.XMaterial
import gg.scala.cookie.settings.builder.MultiOptionPlayerSettingsBuilder
import gg.scala.lemon.player.metadata.Metadata
import gg.scala.tangerine.menu.ConfigurableMenuButtonData
import gg.scala.tangerine.menu.ConfigurableMenuButtonType
import gg.scala.tangerine.menu.ConfigurableMenuHandler
import gg.scala.tangerine.menu.ConfigurableMenuTemplate
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.util.CC
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
            it[it.size] = MultiOptionPlayerSettingsBuilder()
                .titleOf("Action")
                .descriptionOf(
                    "What action would you",
                    "like the button to issue",
                    "on click?"
                )
                .materialOf(XMaterial.COMPASS)
                .orderedValuesOf(
                    *ConfigurableMenuButtonType.values()
                        .map { value -> value.name }.toTypedArray()
                )
                .fallbackOf("NONE")
                .valueOverrideOf { value ->
                    data.menuButtonType = ConfigurableMenuButtonType.valueOf(value)

                    ConfigurableMenuHandler.saveData()
                }
                .providerOverrideOf { _, _ ->
                    Metadata(data.menuButtonType.name)
                }
                .settingOf("n/a").asButton()

            it[it.size] = ItemBuilder(XMaterial.OAK_SIGN)
                .name("${CC.GREEN}Action Data")
                .addToLore(
                    "${CC.GRAY}What would you like",
                    "${CC.GRAY}to use as the input for",
                    "${CC.GRAY}your action?",
                    "",
                    "${CC.WHITE}Example:",
                    "${CC.GRAY}Command: some-commands",
                    "${CC.GRAY}Menu: some-menu",
                    "${CC.GRAY}Queue: a-queue",
                    "",
                    "${CC.YELLOW}Click to modify."
                )
                .toButton { _, _ ->
                    InputPrompt()
                        .withText("${CC.SEC}Please enter the ${CC.PRI}Action Data${CC.SEC}. ${CC.GRAY}(Type \"cancel\" to exit)")
                        .acceptInput { player, input ->
                            if (input.equals("stop", true) || input.equals("cancel", true)) {
                                player.sendMessage("${CC.SEC}You've cancelled the modification operation.")
                                return@acceptInput
                            }

                            player.sendMessage("${CC.SEC}You've set the ${CC.PRI}Action Data${CC.SEC} to ${CC.WHITE}$input${CC.SEC}.")
                            data.menuButtonTypeData = input

                            ConfigurableMenuHandler.saveData()

                            ConfigurableMenuEditorDetailedButtonView(data, template).openMenu(player)
                        }
                        .start(player)
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
