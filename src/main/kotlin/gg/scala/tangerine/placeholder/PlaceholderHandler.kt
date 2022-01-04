package gg.scala.tangerine.placeholder

import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.tangerine.TangerineSpigotPlugin
import gg.scala.tangerine.placeholder.impl.PapiPlaceholderProvider

/**
 * @author GrowlyX
 * @since 9/24/2021
 */
@Service
object PlaceholderHandler {

    private val providers = mutableMapOf<String, PlaceholderProvider>()

    @Configure
    fun configure() {
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
