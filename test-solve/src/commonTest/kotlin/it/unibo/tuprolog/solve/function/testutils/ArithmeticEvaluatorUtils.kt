package it.unibo.tuprolog.solve.function.testutils

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.stdlib.CommonBuiltins
import it.unibo.tuprolog.solve.function.ArithmeticEvaluator
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.TypeError

/**
 * Utils singleton to help testing [ArithmeticEvaluator]
 *
 * @author Enrico
 */
internal object ArithmeticEvaluatorUtils {

    /** A context with [CommonBuiltins] loaded */
    internal val commonFunctionsContext = object : ExecutionContext by ExpressionEvaluatorUtils.noFunctionsContext {
        override val libraries: Libraries = Libraries(CommonBuiltins)
    }

    /** A map from term input to raised error type */
    internal val inputToErrorType by lazy {
        mapOf(
            Var.of("MyVar") to InstantiationError::class,
            Atom.of("PI") to TypeError::class,
            Struct.of("ciao", Integer.of(2)) to TypeError::class,
            Struct.of("/", Integer.of(2), Integer.of(0)) to EvaluationError::class
        )
    }

    /** A map from arithmetic term input to its expected evaluation result */
    internal val inputToResult by lazy {
        mapOf(
            Struct.of("abs", Integer.of(-1)) to Integer.of(1),
            Struct.of("rem", Integer.of(5), Integer.of(2)) to Integer.of(1),
            Struct.of("/", Integer.of(2), Integer.of(4)) to Real.of(0.5)
        )
    }
}
