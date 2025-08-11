package org.example.common

sealed interface TokenType {

    // 1. Keywords

    data object Let : TokenType
    data object Println : TokenType

    // 2. Symbols

    data object Colon : TokenType
    data object Semicolon : TokenType
    data object LeftParen : TokenType
    data object RightParen : TokenType

    // 3. Operators (los incluyo a todos)

    data object Operator : TokenType

    // 4. Variables

    data object Identifier : TokenType
    data object StringLiteral : TokenType
    data object NumberLiteral : TokenType
    //data class ListLiteral(val value: List<String>) : TokenType

    // 5. DataTypes

    sealed interface DataType : TokenType {

        data object String: DataType
        data object Number : DataType
        //Extensible para futuros tipos de datos: maps, sets, list, etc
    }

    // 6. Señalar el fin del programa
    data object EOF : TokenType
}
