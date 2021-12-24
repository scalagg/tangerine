package gg.scala.tangerine.computer.impl

import gg.scala.lemon.Lemon
import gg.scala.lemon.handler.ServerHandler
import gg.scala.tangerine.computer.InformationComputer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * @author GrowlyX
 * @since 12/10/2021
 */
class SingleServerComputer(
    private val serverName: String,
    override var current: String
) : InformationComputer<String>
{
    override suspend fun computeAsync()
    {

    }

    override fun getId() = serverName

    fun launchCoroutineScope(lambda: () -> Unit)
    {
        CoroutineScope(Dispatchers.IO).launch {
            lambda.invoke()
        }
    }
}
