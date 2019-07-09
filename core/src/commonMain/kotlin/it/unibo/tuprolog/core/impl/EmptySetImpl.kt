package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Term

internal object EmptySetImpl : SetImpl(null), EmptySet {

    override val args: Array<Term>
        get() = super<EmptySet>.args

    override val argsList: List<Term>
        get() = super<SetImpl>.argsList

    override val functor: String
        get() = super<EmptySet>.functor

    override val isGround: Boolean
        get() = super<EmptySet>.isGround
}