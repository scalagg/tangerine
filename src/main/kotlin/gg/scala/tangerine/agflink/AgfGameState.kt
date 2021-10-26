package gg.scala.tangerine.agflink

/**
 * @author GrowlyX
 * @since 10/26/2021
 */
enum class AgfGameState
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
