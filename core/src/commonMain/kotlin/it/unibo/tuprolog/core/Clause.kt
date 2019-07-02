package it.unibo.tuprolog.core

import it.unibo.tuprolog.scoping.Scope

interface Clause : Struct {

    override val functor: String
        get() = FUNCTOR

    val head: Struct?

    val body: Term

    override val args: Array<Term>
        get() = (if (head === null) arrayOf(body) else arrayOf(head!!, body))

    override val arity: Int
        get() = (if (head === null) 1 else 2)

    override val isClause: Boolean
        get() = true

    override val isRule: Boolean
        get() = head !== null

    override val isFact: Boolean
        get() = head !== null && body.isTrue

    override val isDirective: Boolean
        get() = head === null

    override fun freshCopy(): Clause = super.freshCopy() as Clause

    override fun freshCopy(scope: Scope): Clause = super.freshCopy(scope) as Clause

    companion object {
        const val FUNCTOR = ":-"

        fun of(head: Struct? = null, vararg body: Term): Clause =
                if (head === null) {
                    Directive.of(body[0], *body.sliceArray(1..body.lastIndex))
                } else {
                    Rule.of(head, *body)
                }
    }

}
