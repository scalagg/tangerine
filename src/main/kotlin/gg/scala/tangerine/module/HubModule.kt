package gg.scala.tangerine.module

import gg.scala.tangerine.module.impl.HubModuleItemAdapter
import net.evilblock.cubed.scoreboard.ScoreboardAdapter

/**
 * @author GrowlyX
 * @since 8/5/2021
 */
interface HubModule {
    val itemAdapter: HubModuleItemAdapter
    val scoreboardAdapter: ScoreboardAdapter
}
