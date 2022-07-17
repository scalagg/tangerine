package gg.scala.tangerine.configurable

/**
 * @author GrowlyX
 * @since 9/17/2021
 */
class ConfigurableMenuTemplate(var id: String)
{
    var title = "Example Size"

    var size = 27
    var placeholder = true
    var placeholderFill = false
    var updateAutomatically = true

    var fallback: String? = null
    var buttons = mutableMapOf<Int, ConfigurableMenuButtonData>()
}
