package dd4.mapmaker

import java.util.concurrent.atomic.AtomicInteger

class Labeller {

    private val sequence = AtomicInteger(0)

    fun currentLabel(): String = sequence.get().toString()

    fun nextLabel(): String = sequence.incrementAndGet().toString()
}
