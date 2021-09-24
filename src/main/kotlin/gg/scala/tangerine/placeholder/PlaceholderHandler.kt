package gg.scala.tangerine.placeholder

import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.placeholder.impl.PapiPlaceholderProvider

/**
 * @author GrowlyX
 * @since 9/24/2021
 */
object PlaceholderHandler {

    private val providers = mutableMapOf<String, PlaceholderProvider>()

    init {
        providers["papi"] = PapiPlaceholderProvider()
    }

    fun findProvider(): PlaceholderProvider? {
        return providers[
                TangerineSpigotPlugin.instance.settings.placeholderProvider
        ]
    }

    fun registerProvider(key: String, placeholderProvider: PlaceholderProvider) {
        providers[key] = placeholderProvider
    }
}
