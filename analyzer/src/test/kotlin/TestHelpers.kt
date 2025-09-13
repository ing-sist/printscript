fun loc(
    line: Int,
    start: Int,
    end: Int,
) = Location(line, start, end)

fun tok(
    lexeme: String,
    line: Int,
    start: Int,
    end: Int,
): Token =
    Token(
        type = TokenType.StringLiteral,
        lexeme = lexeme,
        location = Location(line, start, end),
    )

fun id(
    name: String,
    line: Int = 1,
    start: Int = 1,
    end: Int = start + name.length - 1,
) = IdentifierNode(
    value = tok(name, line, start, end),
    name = name,
)

fun litNumber(
    n: String,
    line: Int = 1,
    start: Int = 1,
) = LiteralNode(
    value = tok(n, line, start, start + n.length - 1),
)

fun litString(
    s: String,
    line: Int = 1,
    start: Int = 1,
) = LiteralNode(
    value = tok("\"$s\"", line, start, start + s.length + 1),
)

fun bin(
    left: AstNode,
    opLex: String,
    right: AstNode,
    line: Int = 1,
    col: Int = 1,
) = BinaryOperationNode(
    left = left,
    operator = tok(opLex, line, col, col),
    right = right,
)

fun unary(
    opLex: String,
    operand: AstNode,
    line: Int = 1,
    col: Int = 1,
) = UnaryOperationNode(
    operator = tok(opLex, line, col, col),
    operand = operand,
)
