package gg.scala.tangerine.computer

/**
 * @author GrowlyX
 * @since 12/10/2021
 */
interface InformationComputer<T>
{
    suspend fun computeAsync()

    fun getId(): String
    var current: T
}
