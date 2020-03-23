package it.unibo.tuprolog.solve.channel

import it.unibo.tuprolog.solve.channel.impl.OutputChannelFromFunction

interface OutputChannel<T> : Channel<T> {
    companion object {
        val stdout: OutputChannel<String>
            get() = stdout()

        val stderr: OutputChannel<String>
            get() = stderr()

        fun <T> of(consumer: (T) -> Unit): OutputChannel<T> = OutputChannelFromFunction(consumer)
    }

    fun write(value: T)
}