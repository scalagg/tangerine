package gg.scala.tangerine.placeholder

import gg.scala.tangerine.placeholder.impl.ScalaPlaceholderProvider

/**
 * @author GrowlyX
 * @since 9/24/2021
 */
object PlaceholderHandler
{
    private var provider: PlaceholderProvider? =
        ScalaPlaceholderProvider

    fun updateProvider(
        provider: PlaceholderProvider
    )
    {
        this.provider = provider
    }

    fun findProvider(): PlaceholderProvider?
    {
        return this.provider
    }
}
