package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal class FactImpl(override val head: Struct)
    : RuleImpl(head, TruthImpl.True), Fact {

    override val isWellFormed: Boolean = true

    override val body: Term
        get() = super<RuleImpl>.body
}