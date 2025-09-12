package rules.implementations

import Token
import TokenType

// fun isIfConditionBeforeLeftBrace(
//    tokens: List<Token>,
//    lbraceIndex: Int,
// ): Boolean {
//    // el anterior tiene que ser )
//    val indexOfClosingParen = previousTokenIndex(lbraceIndex) ?: return false
//    if (tokens[indexOfClosingParen].type !is TokenType.RightParen) return false
//    // si no es ), ya se que no es un if statement
//
//    // hago balanceo (como haciamos con stacks)
//    var parenthesesBalance = 1 // es el que encontre
//    var currentIndex = indexOfClosingParen - 1
//
//    while (currentIndex >= 0) {
//        when (tokens[currentIndex].type) {
//            is TokenType.RightParen -> parenthesesBalance++
//            is TokenType.LeftParen -> {
//                parenthesesBalance--
//
//                if (parenthesesBalance == 0) {
//                    // encontre el ( que abre el if (...)
//                    val indexBeforeParen = previousTokenIndex(currentIndex) ?: return false
//                    return tokens[indexBeforeParen].type is TokenType.If // si el anterior al (..) es un if, true
//                }
//            }
//            else -> {}
//        }
//        currentIndex--
//    }
//
//    // si no balanceo es false
//    return false
// }
//
// private fun previousTokenIndex(i: Int): Int? = if (i - 1 >= 0) i - 1 else null

fun isIfConditionBeforeLeftBrace(
    tokens: List<Token>,
    lbraceIndex: Int,
): Boolean {
    val indexOfClosingParen = previousTokenIndex(lbraceIndex)
    if (indexOfClosingParen == null || tokens[indexOfClosingParen].type !is TokenType.RightParen) {
        return false
    }

    var parenthesesBalance = 1
    var currentIndex = indexOfClosingParen - 1
    var result = false

    while (currentIndex >= 0) {
        when (tokens[currentIndex].type) {
            is TokenType.RightParen -> parenthesesBalance++
            is TokenType.LeftParen -> {
                parenthesesBalance--
                if (parenthesesBalance == 0) {
                    val indexBeforeParen = previousTokenIndex(currentIndex)
                    result = indexBeforeParen != null &&
                        tokens[indexBeforeParen].type is TokenType.If
                    break
                }
            }
            else -> { }
        }
        currentIndex--
    }
    return result
}

private fun previousTokenIndex(i: Int): Int? = if (i - 1 >= 0) i - 1 else null
