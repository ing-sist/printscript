sealed interface Expression {

    data class Binary(val left: Expression, val operator: Token, val right: Expression) : Expression

    data class Literal(val value: Token) : Expression

    data class Variable(val name: Token) : Expression

    data class Grouping(val expression: Expression) : Expression
}