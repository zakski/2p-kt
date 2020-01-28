@file:JsModule("./PrologParser")
@file:JsNonModule

package it.unibo.tuprolog.parser


external class PrologParser(input: dynamic){
    companion object {
        val EOF: Int
        val VARIABLE: Int
        val INTEGER: Int
        val HEX: Int
        val OCT: Int
        val BINARY: Int
        val SIGN: Int
        val FLOAT: Int
        val CHAR: Int
        val BOOL: Int
        val LPAR: Int
        val RPAR: Int
        val LSQUARE: Int
        val RSQUARE: Int
        val EMPTY_LIST: Int
        val LBRACE: Int
        val RBRACE: Int
        val EMPTY_SET: Int
        val SQ_STRING: Int
        val DQ_STRING: Int
        val COMMA: Int
        val PIPE: Int
        val CUT: Int
        val FULL_STOP: Int
        val WHITE_SPACES: Int
        val COMMENT: Int
        val LINE_COMMENT: Int
        val OPERATOR: Int
        val ATOM: Int

        val RULE_singletonTerm: Int
        val RULE_singletonExpression : Int
        val RULE_theory: Int
        val RULE_optClause: Int
        val RULE_clause: Int
        val RULE_expression: Int
        val RULE_outer: Int
        val RULE_op: Int
        val RULE_term: Int
        val RULE_number: Int
        val RULE_integer: Int
        val RULE_real: Int
        val RULE_variable: Int
        val RULE_structure: Int
        val RULE_list: Int
        val RULE_set: Int
    }

    fun singletonTerm(): SingletonTermContext
    fun singletonExpression(): SingletonExpressionContext
    fun theory(): TheoryContext
    fun optClause(): OptClauseContext
    fun clause(): ClauseContext
    fun expression(): ExpressionContext
    fun outer(): OuterContext
    fun op(): OpContext
    fun term(): TermContext
    fun number(): NumberContext
    fun integer(): IntegerContext
    fun real(): RealContext
    fun variable(): VariableContext
    fun structure(): StructureContext
    fun list(): ListContext
    fun set(): SetContext
}

external class SingletonTermContext{
    fun term(): TermContext
}

external class SingletonExpressionContext{
    fun expression(): ExpressionContext

}

external class TheoryContext{
    fun clause(): ClauseContext
}

external class OptClauseContext{
    fun clause(): ClauseContext
}

external class ClauseContext{
    fun expression(): ExpressionContext
}

external class ExpressionContext{
    val priority: Int
    val disabled: Boolean
    val isTerm: Boolean
    val associativity: Associativity
    val bottom: Int
    val left: TermContext
    val _op: OpContext
    val operators: Array<OpContext>
    val _expression: ExpressionContext
    val right: Array<ExpressionContext>
    val _outer: OuterContext
    val outers: Array<OuterContext>

    fun term(): TermContext
    fun op(): OpContext
    fun expression(): ExpressionContext
    fun outer(): OuterContext
}

external class OuterContext{
    val top: Int
    val bottom: Int
    val priority: Int
    val isTerm: Boolean
    val associativity: Associativity

    fun op(): OpContext
    fun expression(): ExpressionContext
    fun outer(): OuterContext
}

external class OpContext{
    val priority: Int
    val associativity: Associativity
    val symbol: Token


}

external class TermContext{
    val isNum: Boolean
    val isVar: Boolean
    val isList: Boolean
    val isStruct: Boolean
    val isExpr: Boolean
    val isSet: Boolean
    fun variable(): VariableContext
    fun structure(): StructureContext
    fun list(): ListContext
    fun set(): SetContext
    fun number(): NumberContext
    fun expression(): ExpressionContext
}

external class NumberContext{
    val isInt: Boolean
    val isReal: Boolean
    fun integer(): IntegerContext
    fun real(): RealContext
}

external class IntegerContext{
    val isHex: Boolean
    val isOct: Boolean
    val isBin: Boolean
    val isChar: Boolean
    val value: Token
    val sign: Token
}

external class RealContext{
    val value: Token
    val sign: Token
}

external class VariableContext{
    val isAnonymous: Boolean
    val value: Token
}

external class StructureContext{
    val arity: Int
    val isTruth: Boolean
    val isList: Boolean
    val isSet: Boolean
    val isString: Boolean
    val isCut: Boolean
    val functor: Token
    val _expression: ExpressionContext
    val args: Array<ExpressionContext>

    fun expression(): ExpressionContext
}

external class ListContext{
    val length: Int
    val hasTail: Boolean
    val _expression: ExpressionContext
    val items: Array<ExpressionContext>
    val tail: ExpressionContext

    fun expression(): ExpressionContext
}

external class SetContext{
    val length: Int
    val _expression: ExpressionContext
    val items: Array<ExpressionContext>
    fun expression(): ExpressionContext
}

