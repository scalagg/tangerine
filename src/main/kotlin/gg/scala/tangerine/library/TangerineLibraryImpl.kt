package gg.scala.tangerine.library

import gg.scala.commons.library.model.Library
import gg.scala.lemon.library.LemonLibraryImpl

/**
 * @author GrowlyX
 * @since 10/15/2021
 */
object TangerineLibraryImpl
{

    @JvmStatic
    val CONFIG_API = LemonLibraryImpl.CONFIG_API

    @JvmStatic
    val PAPER_LIB = Library("io.papermc", "paperlib", "1.0.6")
        .repository("https://papermc.io/repo/repository/maven-public/")

    @JvmStatic
    val TAB_API = Library("com.github.GrowlyX", "TabAPI", "e01fba589e")
        .repository("https://jitpack.io")

    @JvmStatic
    val XENON_PARTICLES = Library("xyz.xenondevs", "particle", "1.6.6")

}
