sealed interface TokenType {
    sealed interface Keyword : TokenType {
        data object VariableDeclaration : Keyword

        data object If : Keyword

        data object Else : Keyword

        data object ConstDeclaration : Keyword
    }

    sealed interface Operator : TokenType {
        companion object {
            private val operators = mutableListOf<TokenType>()
            val all: List<TokenType> get() = operators

            fun register(op: TokenType) {
                operators += op
            }
        }

        data object Plus : Operator {
            init {
                Operator.register(this)
            }
        }

        data object Minus : Operator {
            init {
                Operator.register(this)
            }
        }

        data object Multiply : Operator {
            init {
                Operator.register(this)
            }
        }

        data object Divide : Operator {
            init {
                Operator.register(this)
            }
        }

        data object Equals : Operator {
            init {
                Operator.register(this)
            }
        }

        data object NotEquals : Operator {
            init {
                Operator.register(this)
            }
        }

        data object LessThan : Operator {
            init {
                Operator.register(this)
            }
        }

        data object LessThanOrEqual : Operator {
            init {
                Operator.register(this)
            }
        }

        data object GreaterThan : Operator {
            init {
                Operator.register(this)
            }
        }

        data object GreaterThanOrEqual : Operator {
            init {
                Operator.register(this)
            }
        }

    }

    data object Assignment : TokenType

    data object StringType : TokenType

    data object NumberType : TokenType

    data object BooleanType : TokenType

    data object FunctionCall : TokenType

    // 2. Symbols
    data object Colon : TokenType

    data object Semicolon : TokenType

    data object LeftParen : TokenType

    data object RightParen : TokenType

    data object LeftBrace : TokenType

    data object RightBrace : TokenType

    data object Comma : TokenType

    // 4. Variables
    data object Identifier : TokenType

    data object StringLiteral : TokenType

    data object NumberLiteral : TokenType

    data object BooleanLiteral : TokenType // true/false (new in 1.1)

    // 6. End of file
    data object EOF : TokenType

    data object Space : TokenType

    data object ERROR : TokenType

    data object Newline : TokenType
}
