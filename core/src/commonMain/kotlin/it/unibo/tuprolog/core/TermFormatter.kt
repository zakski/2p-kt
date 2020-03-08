package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.SimpleTermFormatter
import it.unibo.tuprolog.core.impl.TermFormatterWithPrettyExpressions
import it.unibo.tuprolog.core.impl.TermFormatterWithPrettyVariables
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.jvm.JvmStatic

interface TermFormatter : Formatter<Term>, TermVisitor<String> {
    override fun format(value: Term): String {
        return value.accept(this)
    }

    companion object {
        @JvmStatic
        val withPrettyVariables: TermFormatter
            get() = TermFormatterWithPrettyVariables()

        @JvmStatic
        val withPrologDefaults: TermFormatter
            get() = withPrettyExpressions(OperatorSet.DEFAULT)

        @JvmStatic
        fun withPrettyExpressions(prettyVariables: Boolean, operatorSet: OperatorSet): TermFormatter {
            return if (prettyVariables) {
                TermFormatterWithPrettyExpressions(TermFormatterWithPrettyVariables(), operatorSet)
            } else {
                TermFormatterWithPrettyExpressions(SimpleTermFormatter, operatorSet)
            }
        }

        @JvmStatic
        fun withPrettyExpressions(operatorSet: OperatorSet): TermFormatter {
            return withPrettyExpressions(true, operatorSet)
        }

        @JvmStatic
        fun withPrettyExpressions(prettyVariables: Boolean): TermFormatter {
            return withPrettyExpressions(prettyVariables, OperatorSet.DEFAULT)
        }
    }
}