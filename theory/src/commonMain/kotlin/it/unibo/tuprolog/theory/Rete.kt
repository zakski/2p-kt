package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unification.Companion.matches
import kotlin.math.min

sealed class ReteTree<K>(open val children: MutableMap<K, out ReteTree<*>> = mutableMapOf()) {

    data class RootNode(override val children: MutableMap<String?, ReteTree<*>> = mutableMapOf())
        : ReteTree<String?>(children) {

        override val header: String
            get() = "Root"

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            val child: ReteTree<*>? = when (clause) {
                is Directive -> {
                    children[null]
                }
                is Rule -> {
                    children[clause.head.functor]
                }
                else -> throw IllegalStateException()
            }

            return child?.remove(clause, limit) ?: emptySequence()
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Directive -> {
                    var child: DirectiveNode? = children[null] as DirectiveNode?

                    if (child === null) {
                        child = DirectiveNode()
                        children[null] = child
                    }

                    child.put(clause, before)
                }
                is Rule -> {
                    val functor: String = clause.head.functor
                    var child: FunctorNode? = children[functor] as FunctorNode?

                    if (child === null) {
                        child = FunctorNode(functor)
                        children[functor] = child
                    }
                    child.put(clause, before)
                }
            }
        }

        override fun clone(): RootNode {
            return RootNode(children.clone({ it }, { it.clone() }))
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when (clause) {
                is Directive -> {
                    children[null]?.get(clause) ?: emptySequence()
                }
                is Rule -> {
                    children[clause.head.functor]?.get(clause) ?: emptySequence()
                }
                else -> emptySequence()
            }
        }
    }

    data class DirectiveNode(val directives: MutableList<Directive> = mutableListOf())
        : ReteTree<Nothing>() {

        override val header: String
            get() = "Directives"

        override val clauses: Sequence<Clause>
            get() = directives.asSequence()

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            val toTake = if (limit > 0) min(limit, directives.size) else directives.size
            val result = mutableListOf<Clause>()
            val i = directives.iterator()
            var j = 0
            while (i.hasNext() && j < toTake) {
                with(i.next()) {
                    if (this.matches(clause)) {
                        result.add(this)
                        i.remove()
                        j++
                    }
                }
            }
            return result.asSequence()
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Directive -> if (before) directives.add(0, clause) else directives.add(clause)
            }
        }

        override fun clone(): DirectiveNode {
            return DirectiveNode(directives.map { it }.toMutableList())
        }

        override fun toString(treefy: Boolean): String {
            return if (treefy) {
                "$header {" +
                        directives.joinToString(".\n\t", "\n\t", ".\n") +
                        "}"
            } else {
                toString()
            }
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when (clause) {
                is Directive -> directives.asSequence().filter { it matches clause }
                else -> emptySequence()
            }
        }

    }

    data class FunctorNode(val functor: String, override val children: MutableMap<Int, ArityNode> = mutableMapOf())
        : ReteTree<Int>(children) {

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            return when {
                limit == 0 -> {
                    emptySequence()
                }
                clause is Rule -> {
                    children[clause.arity]?.remove(clause, limit) ?: emptySequence()
                }
                else -> emptySequence()
            }
        }

        override val header: String
            get() = "Functor($functor)"

        override fun put(clause: Clause, before: Boolean) {
            when {
                clause is Rule && functor == clause.head.functor -> {
                    val arity: Int = clause.head.arity
                    var child: ArityNode? = children[arity]

                    if (child === null) {
                        child = ArityNode(arity)
                        children[arity] = child
                    }
                    child.put(clause, before)
                }
            }
        }

        override fun clone(): FunctorNode {
            return FunctorNode(functor, children.clone({ it }, { it.clone() }))
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when (clause) {
                is Rule -> {
                    children[clause.head.arity]?.get(clause) ?: emptySequence()
                }
                else -> emptySequence()
            }
        }
    }

    data class ArityNode(val arity: Int, override val children: MutableMap<Term?, ReteTree<*>> = mutableMapOf())
        : ReteTree<Term?>(children) {

        override val header: String
            get() = "Arity($arity)"

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            return when {
                limit == 0 || clause !is Rule -> {
                    emptySequence()
                }
                clause.head.arity > 0 -> {
                    val firstArg: Term = clause.head[0]

                    val removed: MutableList<Clause> = mutableListOf()
                    for (child in children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(firstArg) }
                            .map { it.value }) {

                        removed += child.remove(clause, limit - removed.size)
                        if (removed.size == limit) break
                    }

                    removed.asSequence()
                }
                else -> {
                    children[null]?.remove(clause, limit) ?: emptySequence()
                }
            }
        }

        override fun put(clause: Clause, before: Boolean) {
            when {
                clause !is Rule -> {
                    return
                }
                clause.head.arity > 0 -> {
                    val firstArg: Term = clause.head[0]
                    var child: ArgNode? = children[firstArg] as ArgNode?

                    if (child === null) {
                        child = children.entries.asSequence()
                                .filter { it.key !== null }
                                .filter { it.value is ArgNode }
                                .find { it.key!! structurallyEquals firstArg }
                                ?.value as ArgNode?
                    }

                    if (child === null) {
                        child = ArgNode(0, firstArg)
                        children[firstArg] = child
                    }

                    child.put(clause, before)
                }
                else -> {
                    var child: NoArgsNode? = children[null] as NoArgsNode?

                    if (child === null) {
                        child = NoArgsNode()
                        children[null] = child
                    }

                    child.put(clause, before)
                }
            }
        }

        override fun clone(): ArityNode {
            return ArityNode(arity, children.clone({ it }, { it.clone() }))
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when {
                clause !is Rule -> {
                    emptySequence()
                }
                clause.head.arity > 0 -> {
                    val firstArg: Term = clause.head[0]

                    children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(firstArg) }
                            .map { it.value }
                            .flatMap { it.get(clause) }
                }
                else -> {
                    children[null]?.get(clause) ?: emptySequence()
                }
            }
        }
    }

    data class NoArgsNode(override val children: MutableMap<Nothing?, RuleNode> = mutableMapOf())
        : ReteTree<Nothing?>(children) {

        override val header: String
            get() = "NoArguments"

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            return when {
                limit == 0 || children.isEmpty() -> {
                    emptySequence()
                }
                clause is Rule -> {
                    children[null]?.remove(clause, limit) ?: emptySequence()
                }
                else -> emptySequence()
            }
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Rule -> {
                    var child: RuleNode? = children[null]

                    if (child == null) {
                        child = RuleNode()
                        children[null] = child
                    }

                    child.put(clause, before)
                }
            }
        }

        override fun clone(): NoArgsNode {
            return NoArgsNode(children.clone({ it }, { it.clone() }))
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when (clause) {
                is Rule -> {
                    children[null]?.get(clause) ?: emptySequence()
                }
                else -> emptySequence()
            }
        }
    }

    data class ArgNode(val index: Int, val term: Term, override val children: MutableMap<Term?, ReteTree<*>> = mutableMapOf())
        : ReteTree<Term?>(children) {

        override val header: String
            get() = "Argument($index, $term)"

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            return when {
                limit == 0 || clause !is Rule -> {
                    emptySequence()
                }
                index < clause.head.arity - 1 -> {
                    val nextArg: Term = clause.head[index + 1]

                    val removed: MutableList<Clause> = mutableListOf()
                    for (child in children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(nextArg) }
                            .map { it.value }) {

                        removed += child.remove(clause, limit - removed.size)
                        if (removed.size == limit) break
                    }

                    removed.asSequence()
                }
                else -> {
                    children[null]?.remove(clause, limit) ?: emptySequence()
                }
            }
        }

        override fun put(clause: Clause, before: Boolean) {
            when {
                clause !is Rule -> {
                    return
                }
                index < clause.head.arity - 1 -> {
                    val nextArg: Term = clause.head[index + 1]
                    var child: ArgNode? = children[nextArg] as ArgNode?

                    if (child === null) {
                        child = children.entries.asSequence()
                                .filter { it.key !== null }
                                .filter { it.value is ArgNode }
                                .find { it.key!! structurallyEquals nextArg }
                                ?.value as ArgNode?
                    }

                    if (child === null) {
                        child = ArgNode(index + 1, nextArg)
                        children[nextArg] = child
                    }

                    child.put(clause, before)
                }
                else -> {
                    var child: RuleNode? = children[null] as RuleNode?

                    if (child === null) {
                        child = RuleNode()
                        children[null] = child
                    }

                    child.put(clause, before)
                }
            }
        }

        override fun clone(): ArgNode {
            return ArgNode(index, term, children.clone({ it }, { it.clone() }))
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return when {
                clause !is Rule -> {
                    emptySequence()
                }
                index < clause.head.arity - 1 -> {
                    val nextArg: Term = clause.head[index + 1]

                    children.entries.asSequence()
                            .filter { it.key !== null }
                            .filter { it.value is ArgNode }
                            .filter { it.key!!.matches(nextArg) }
                            .map { it.value }
                            .flatMap { it.get(clause) }
                }
                else -> {
                    children[null]?.get(clause) ?: emptySequence()
                }
            }
        }
    }

    data class RuleNode(val rules: MutableList<Rule> = mutableListOf()) : ReteTree<Nothing>() {

        override val header: String
            get() = "Rules"

        override val clauses: Sequence<Clause>
            get() = rules.asSequence()

        override fun remove(clause: Clause, limit: Int): Sequence<Clause> {
            if (limit == 0) {
                return emptySequence()
            }

            val toTake = if (limit > 0) min(limit, rules.size) else rules.size
            val result = mutableListOf<Clause>()
            val i = rules.iterator()
            var j = 0
            while (i.hasNext() && j < toTake) {
                with(i.next()) {
                    if (this.matches(clause)) {
                        result.add(this)
                        i.remove()
                        j++
                    }
                }
            }
            return result.asSequence()
        }

        override fun put(clause: Clause, before: Boolean) {
            when (clause) {
                is Rule -> {
                    if (before) rules.add(0, clause) else rules.add(clause)
                }
            }
        }

        override fun clone(): RuleNode {
            return RuleNode(rules.map { it }.toMutableList())
        }

        override fun get(clause: Clause): Sequence<Clause> {
            return rules.asSequence().filter { it matches clause }
        }

        override fun toString(treefy: Boolean): String {
            return if (treefy) {
                "$header {" +
                        rules.joinToString(".\n\t", "\n\t", ".\n") +
                        "}"
            } else {
                toString()
            }
        }
    }

    abstract fun clone(): ReteTree<K>

    internal abstract fun put(clause: Clause, before: Boolean = false)

    internal abstract fun remove(clause: Clause, limit: Int = 1): Sequence<Clause>

    internal fun removeAll(clause: Clause): Sequence<Clause> {
        return remove(clause, Int.MAX_VALUE)
    }

    abstract fun get(clause: Clause): Sequence<Clause>

    open fun toString(treefy: Boolean): String {
        return if (treefy) {
            "$header {" +
                    children.values.joinToString(",\n\t", "\n\t", "\n") {
                        it.toString(treefy).replace("\n", "\n\t")
                    } +
                    "}"
        } else {
            toString()
        }
    }

    open val clauses: Sequence<Clause>
        get() = children.asSequence().flatMap { it.value.clauses }


    protected abstract val header: String

    companion object {

        private fun <K, V> MutableMap<K, V>.clone(cloneKey: (K)-> K, cloneValue: (V)-> V): MutableMap<K, V> {
            return entries.map { cloneKey(it.key) to cloneValue(it.value) }.toMap(mutableMapOf())
        }

        fun of(clauses: Iterable<Clause>): ReteTree<*> {
            return RootNode().apply {
                for (clause in clauses) {
                    put(clause)
                }
            }
        }

        fun of(vararg clauses: Clause): ReteTree<*> {
            return of(listOf(*clauses))
        }
    }
}
