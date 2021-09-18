package gg.scala.tangerine.menu

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
class ConfigurableMenuTemplate(val id: String) {

    var title = "Example Size"
    var size = 27
    var buttons = mutableMapOf<Int, ConfigurableMenuButtonData>()

}
