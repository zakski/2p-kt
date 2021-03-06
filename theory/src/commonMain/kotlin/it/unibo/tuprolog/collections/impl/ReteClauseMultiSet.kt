package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.*
import it.unibo.tuprolog.collections.AbstractReteClauseCollection
import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.TheoryUtils
import it.unibo.tuprolog.utils.itemWiseHashCode

internal class ReteClauseMultiSet private constructor(
    rete: ReteTree
) : ClauseMultiSet, AbstractReteClauseCollection<ReteClauseMultiSet>(rete) {

    private val hashCodeCache by lazy {
        ClauseMultiSet.hashCode(this)
    }

    init {
        require(!rete.isOrdered)
    }

    /** Construct a [ReteClauseMultiSet] from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteTree.unordered(clauses)) {
        TheoryUtils.checkClausesCorrect(clauses)
    }

    override fun count(clause: Clause): Long =
        rete.get(clause).count().toLong()

    override fun get(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun newCollectionBuilder(rete: ReteTree): ReteClauseMultiSet {
        return ReteClauseMultiSet(rete)
    }

    override fun equals(other: Any?): Boolean {
        return if (other is ClauseMultiSet && other !is MutableClauseMultiSet) {
            ClauseMultiSet.equals(this, other)
        } else {
            false
        }
    }

    override fun hashCode(): Int = hashCodeCache

}