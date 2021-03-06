package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.solve.ClassicExecutionContext
import it.unibo.tuprolog.solve.Solution

internal data class StateBacktracking(override val context: ClassicExecutionContext) : AbstractState(context) {
    override fun computeNext(): State {
        val choicePoints = context.choicePoints
        return if (choicePoints.let { it === null || !it.hasOpenAlternatives }) {
            StateEnd(
                solution = Solution.No(context.query),
                context = context.copy(step = nextStep())
            )
        } else {
            val choicePointContext = choicePoints!!.pathToRoot.first { it.alternatives.hasNext }
            val nextContext = choicePointContext.backtrack(context)
            if (nextContext.primitives.hasNext) {
                StatePrimitiveExecution(nextContext)
            } else {
                StateRuleExecution(nextContext)
            }
        }
    }
}