package it.unibo.tuprolog.libraries.stdlib.primitive

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.primitive.PrimitiveWrapper
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.solve.solver.ExecutionContextImpl
import it.unibo.tuprolog.solve.solver.SideEffectManagerImpl
import it.unibo.tuprolog.solve.solver.SolverSLD
import it.unibo.tuprolog.solve.solver.SolverUtils
import it.unibo.tuprolog.solve.solver.SolverUtils.newSolveRequest
import it.unibo.tuprolog.solve.solver.SolverUtils.responseBy

/**
 * Implementation of primitive handling `call/1` behaviour
 *
 * @author Enrico
 */
internal object Call : PrimitiveWrapper<ExecutionContextImpl>("call", 1) {

    override fun uncheckedImplementation(request: Solve.Request<ExecutionContextImpl>): Sequence<Solve.Response> =
            request.ensuringAllArgumentsAreInstantiated()
                    .arguments.single()
                    .let { toBeCalledGoal ->
                        when {
                            SolverUtils.isWellFormed(toBeCalledGoal) ->
                                SolverSLD().solve(
                                        request.newSolveRequest(toBeCalledGoal as Struct)
                                ).map {
                                    request.responseBy(
                                            it.copy(sideEffectManager =
                                            (it.sideEffectManager as? SideEffectManagerImpl)
                                                    ?.resetCutWorkChanges(request.context.sideEffectManager)
                                                    ?: it.sideEffectManager
                                            )
                                    )
                                }

                            else -> throw TypeError(
                                    message = "call/1 argument is neither a Variable nor a well-formed goal",
                                    context = request.context,
                                    expectedType = TypeError.Expected.CALLABLE,
                                    actualValue = toBeCalledGoal
                            )
                        }
                    }

}