package gg.scala.tangerine.library

import gg.scala.commons.annotations.LibraryLoader
import gg.scala.commons.library.model.Library

/**
 * @author GrowlyX
 * @since 10/15/2021
 */
@Library(
    groupId = "io.papermc",
    artifactId = "paperlib",
    version = "1.0.6",
    repository = "https://papermc.io/repo/repository/maven-public/"
)
@Library(
    groupId = "xyz.xenondevs",
    artifactId = "particle",
    version = "1.7",
)
@LibraryLoader
object TangerineLibraryImpl
