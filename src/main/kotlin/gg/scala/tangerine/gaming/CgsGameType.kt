package gg.scala.tangerine.gaming

/**
 * @author GrowlyX
 * @since 10/26/2021
 */
enum class CgsGameType(
    val formatted: String,
    val group: String
)
{
    UHC_MEETUP("UHC Meetup", "uhc-meetup"),
    UHC("UHC", "uhc"),
    SKYWARS("SkyWars", "skywars"),
    BEDWARS("BedWars", "bedwars"),
    BUNKERS("Bunkers", "bunkers"),
    POT_SG("Pot SG", "pot-sg"),
    SURVIVAL_GAMES("Survival Games", "survival-games");

    companion object
    {
        @JvmStatic
        val ALL = values()
    }
}
