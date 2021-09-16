package com.solexgames.tangerine.module

import com.solexgames.tangerine.module.impl.HubModuleItemAdapter
import net.evilblock.cubed.scoreboard.ScoreboardOverride

/**
 * @author GrowlyX
 * @since 8/5/2021
 */
interface HubModule {

    val itemAdapter: HubModuleItemAdapter?
    val scoreboardAdapter: ScoreboardOverride?

}
