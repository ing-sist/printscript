# Expression Builder Tests - Complex Expressions

## Arithmetic Precedence Tests

### Test: addition_multiplication_precedence
INPUT:
result = 2 + 3 * 4;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = LiteralNode(value = Token(NumberLiteral, "2")),
        operator = Token(Plus, "+"),
        right = BinaryOperationNode(
            left = LiteralNode(value = Token(NumberLiteral, "3")),
            operator = Token(Multiply, "*"),
            right = LiteralNode(value = Token(NumberLiteral, "4"))
        )
    )
)

### Test: multiplication_division_left_associative
INPUT:
result = 8 / 2 * 3;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = LiteralNode(value = Token(NumberLiteral, "8")),
            operator = Token(Divide, "/"),
            right = LiteralNode(value = Token(NumberLiteral, "2"))
        ),
        operator = Token(Multiply, "*"),
        right = LiteralNode(value = Token(NumberLiteral, "3"))
    )
)

### Test: complex_precedence_expression
INPUT:
result = 1 + 2 * 3 - 4 / 2;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = LiteralNode(value = Token(NumberLiteral, "1")),
            operator = Token(Plus, "+"),
            right = BinaryOperationNode(
                left = LiteralNode(value = Token(NumberLiteral, "2")),
                operator = Token(Multiply, "*"),
                right = LiteralNode(value = Token(NumberLiteral, "3"))
            )
        ),
        operator = Token(Minus, "-"),
        right = BinaryOperationNode(
            left = LiteralNode(value = Token(NumberLiteral, "4")),
            operator = Token(Divide, "/"),
            right = LiteralNode(value = Token(NumberLiteral, "2"))
        )
    )
)

## Parentheses Override Precedence

### Test: parentheses_override_precedence
INPUT:
result = (1 + 2) * 3;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = LiteralNode(value = Token(NumberLiteral, "1")),
            operator = Token(Plus, "+"),
            right = LiteralNode(value = Token(NumberLiteral, "2"))
        ),
        operator = Token(Multiply, "*"),
        right = LiteralNode(value = Token(NumberLiteral, "3"))
    )
)

### Test: nested_parentheses
INPUT:
result = ((a + b) * c) / d;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = BinaryOperationNode(
                left = IdentifierNode(value = Token(Identifier, "a"), name = "a"),
                operator = Token(Plus, "+"),
                right = IdentifierNode(value = Token(Identifier, "b"), name = "b")
            ),
            operator = Token(Multiply, "*"),
            right = IdentifierNode(value = Token(Identifier, "c"), name = "c")
        ),
        operator = Token(Divide, "/"),
        right = IdentifierNode(value = Token(Identifier, "d"), name = "d")
    )
)

## String Concatenation Expressions

### Test: string_number_concatenation
INPUT:
message = "Value: " + 42 + " units";
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "message"), name = "message"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = LiteralNode(value = Token(StringLiteral, "\"Value: \"")),
            operator = Token(Plus, "+"),
            right = LiteralNode(value = Token(NumberLiteral, "42"))
        ),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(StringLiteral, "\" units\""))
    )
)

### Test: mixed_string_arithmetic_concatenation
INPUT:
result = "Result: " + (a + b) + " total";
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = LiteralNode(value = Token(StringLiteral, "\"Result: \"")),
            operator = Token(Plus, "+"),
            right = BinaryOperationNode(
                left = IdentifierNode(value = Token(Identifier, "a"), name = "a"),
                operator = Token(Plus, "+"),
                right = IdentifierNode(value = Token(Identifier, "b"), name = "b")
            )
        ),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(StringLiteral, "\" total\""))
    )
)

## Unary Expressions

### Test: unary_minus_with_parentheses
INPUT:
x = -(a + b);
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = UnaryOperationNode(
        operator = Token(Minus, "-"),
        operand = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "a"), name = "a"),
            operator = Token(Plus, "+"),
            right = IdentifierNode(value = Token(Identifier, "b"), name = "b")
        )
    )
)

### Test: unary_plus_identifier
INPUT:
x = +value;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = UnaryOperationNode(
        operator = Token(Plus, "+"),
        operand = IdentifierNode(value = Token(Identifier, "value"), name = "value")
    )
)

### Test: multiple_unary_operators
INPUT:
x = --value;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = UnaryOperationNode(
        operator = Token(Minus, "-"),
        operand = UnaryOperationNode(
            operator = Token(Minus, "-"),
            operand = IdentifierNode(value = Token(Identifier, "value"), name = "value")
        )
    )
)

## Decimal Numbers in Expressions

### Test: decimal_arithmetic
INPUT:
result = 3.14 * radius * radius;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = LiteralNode(value = Token(NumberLiteral, "3.14")),
            operator = Token(Multiply, "*"),
            right = IdentifierNode(value = Token(Identifier, "radius"), name = "radius")
        ),
        operator = Token(Multiply, "*"),
        right = IdentifierNode(value = Token(Identifier, "radius"), name = "radius")
    )
)

### Test: mixed_integer_decimal_arithmetic
INPUT:
result = 10 / 3.0 + 1.5;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = LiteralNode(value = Token(NumberLiteral, "10")),
            operator = Token(Divide, "/"),
            right = LiteralNode(value = Token(NumberLiteral, "3.0"))
        ),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(NumberLiteral, "1.5"))
    )
)

## Single Element Expressions

### Test: single_number_expression
INPUT:
x = 42;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = LiteralNode(value = Token(NumberLiteral, "42"))
)

### Test: single_identifier_expression
INPUT:
x = value;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = IdentifierNode(value = Token(Identifier, "value"), name = "value")
)

### Test: single_string_expression
INPUT:
x = "hello";
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = LiteralNode(value = Token(StringLiteral, "\"hello\""))
)

### Test: single_boolean_expression
INPUT:
x = true;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = LiteralNode(value = Token(BooleanLiteral, "true"))
)

## Complex Nested Expressions in Function Calls

### Test: complex_expression_in_println
INPUT:
println("Total: " + (price * quantity + tax));
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = BinaryOperationNode(
        left = LiteralNode(value = Token(StringLiteral, "\"Total: \"")),
        operator = Token(Plus, "+"),
        right = BinaryOperationNode(
            left = BinaryOperationNode(
                left = IdentifierNode(value = Token(Identifier, "price"), name = "price"),
                operator = Token(Multiply, "*"),
                right = IdentifierNode(value = Token(Identifier, "quantity"), name = "quantity")
            ),
            operator = Token(Plus, "+"),
            right = IdentifierNode(value = Token(Identifier, "tax"), name = "tax")
        )
    ),
    isVoid = true
)
