package builders.expresionHelpers

import AstNode
import FunctionCallNode
import Result
import Token
import TokenType
import builders.ExpressionBuilder
import parser.ParseError
import java.util.LinkedList
import java.util.Queue
import java.util.Stack

class ShuntingYardConverter(
    private val precedenceManager: OperatorPrecedenceManager,
) {
    // Non-throwing API with single-exit to satisfy ReturnCount
    fun convertToRPNResult(tokens: List<Token>): Result<Queue<Any>, ParseError> {
        val outputQueue: Queue<Any> = LinkedList()
        val operatorStack: Stack<Token> = Stack()

        var error: ParseError? = null
        var i = 0

        while (i < tokens.size && error == null) {
            val token = tokens[i]
            when (token.type) {
                is TokenType.NumberLiteral,
                is TokenType.StringLiteral,
                is TokenType.BooleanLiteral,
                is TokenType.Identifier,
                -> outputQueue.add(AstNodeFactory().createFromToken(token))

                is TokenType.FunctionCall -> {
                    if (i + 1 >= tokens.size || tokens[i + 1].type !is TokenType.LeftParen) {
                        error = ParseError.InvalidSyntax(listOf(tokens[i]), "Function call must be followed by '('")
                    } else {
                        when (val parsed = parseFunctionAsAstResult(tokens, i)) {
                            is Result.Success -> {
                                val (fnNode, nextIndex) = parsed.value
                                outputQueue.add(fnNode)
                                i = nextIndex - 1 // -1 because i++ happens at end of loop
                            }
                            is Result.Failure -> {
                                error = parsed.error
                            }
                        }
                    }
                }

                is TokenType.LeftParen -> operatorStack.push(token)

                is TokenType.RightParen -> processRightParenthesis(operatorStack, outputQueue)

                else -> processOperator(token, operatorStack, outputQueue)
            }
            i++
        }

        return if (error != null) {
            Result.Failure(error)
        } else {
            while (operatorStack.isNotEmpty()) {
                outputQueue.add(operatorStack.pop())
            }
            Result.Success(outputQueue)
        }
    }

    // Reduced complexity by extracting helper methods
    private fun parseFunctionAsAstResult(
        tokens: List<Token>,
        start: Int,
    ): Result<Pair<AstNode, Int>, ParseError> {
        val functionName = tokens[start].lexeme

        if (functionName == "println") {
            return Result.Failure(
                ParseError.InvalidSyntax(
                    listOf(tokens[start]),
                    "println cannot be used in expressions",
                ),
            )
        }

        val argTokens = collectArgumentTokens(tokens, start)
        return buildFunctionNode(tokens, start, argTokens, functionName)
    }

    private fun collectArgumentTokens(
        tokens: List<Token>,
        start: Int,
    ): List<Token> {
        var j = start + 2
        var depth = 0
        val argTokens = mutableListOf<Token>()

        while (j < tokens.size) {
            val t = tokens[j]
            when (t.type) {
                is TokenType.LeftParen -> {
                    depth++
                    argTokens.add(t)
                }
                is TokenType.RightParen -> {
                    if (depth == 0) break
                    depth--
                    argTokens.add(t)
                }
                else -> argTokens.add(t)
            }
            j++
        }
        return argTokens
    }

    private fun buildFunctionNode(
        tokens: List<Token>,
        start: Int,
        argTokens: List<Token>,
        functionName: String,
    ): Result<Pair<AstNode, Int>, ParseError> {
        val j = start + 2 + argTokens.size

        if (j >= tokens.size || tokens[j].type !is TokenType.RightParen) {
            return Result.Failure(
                ParseError.InvalidSyntax(
                    listOf(tokens.getOrNull(j - 1) ?: tokens[start]),
                    "Unclosed function call",
                ),
            )
        }

        val argAst = createArgumentAst(argTokens, tokens[start])
        return when (argAst) {
            is Result.Success -> {
                val node =
                    FunctionCallNode(
                        functionName = functionName,
                        content = argAst.value,
                        isVoid = false,
                    )
                Result.Success(Pair(node, j + 1))
            }
            is Result.Failure -> argAst
        }
    }

    private fun createArgumentAst(
        argTokens: List<Token>,
        functionToken: Token,
    ): Result<AstNode, ParseError> =
        if (argTokens.isEmpty()) {
            val emptyStringToken = Token(TokenType.StringLiteral, "", functionToken.location)
            Result.Success(AstNodeFactory().createFromToken(emptyStringToken))
        } else {
            ExpressionBuilder().buildResult(argTokens)
        }

    private fun processRightParenthesis(
        operatorStack: Stack<Token>,
        outputQueue: Queue<Any>,
    ) {
        while (operatorStack.isNotEmpty() && operatorStack.peek().type !is TokenType.LeftParen) {
            outputQueue.add(operatorStack.pop())
        }
        if (operatorStack.isNotEmpty()) {
            operatorStack.pop()
        }
    }

    private fun processOperator(
        token: Token,
        operatorStack: Stack<Token>,
        outputQueue: Queue<Any>,
    ) {
        while (shouldPopOperator(token, operatorStack)) {
            outputQueue.add(operatorStack.pop())
        }
        operatorStack.push(token)
    }

    private fun shouldPopOperator(
        currentToken: Token,
        operatorStack: Stack<Token>,
    ): Boolean =
        operatorStack.isNotEmpty() &&
            operatorStack.peek().type !is TokenType.LeftParen &&
            precedenceManager.hasHigherOrEqualPrecedence(
                operatorStack.peek().type,
                currentToken.type,
            )
}
