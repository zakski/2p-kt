package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Term

/**
 * [Indicator] default implementation
 *
 * @author Enrico
 */
internal class IndicatorImpl(override val nameTerm: Term, override val arityTerm: Term) :
    StructImpl(Indicator.FUNCTOR, arrayOf(nameTerm, arityTerm)), Indicator {

    override val functor: String = Indicator.FUNCTOR

    override val args: Array<Term> by lazy { super<StructImpl>.args }

    override val indicatedName: String? by lazy { super.indicatedName }

    override val indicatedArity: Int? by lazy { super.indicatedArity }

    override fun toString(): String = "$nameTerm${Indicator.FUNCTOR}$arityTerm"
}
