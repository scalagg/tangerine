package gg.scala.tangerine.gaming

/**
 * @author GrowlyX
 * @since 10/26/2021
 */
enum class CgsGameState
{
    ENDING,
    WAITING,
    STARTING,
    IN_GAME;

    companion object
    {
        @JvmStatic
        val ALL = values()
    }
}
