# Assignment Tests - PrintScript 1.0 & 1.1

## Basic Assignments

### Test: simple_number_assignment
INPUT:
x = 42;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = LiteralNode(value = Token(NumberLiteral, "42"))
)

### Test: simple_string_assignment
INPUT:
name = "John";
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "name"), name = "name"),
    expression = LiteralNode(value = Token(StringLiteral, "\"John\""))
)

### Test: boolean_literal_assignment
INPUT:
flag = true;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "flag"), name = "flag"),
    expression = LiteralNode(value = Token(BooleanLiteral, "true"))
)

### Test: boolean_false_assignment
INPUT:
isValid = false;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "isValid"), name = "isValid"),
    expression = LiteralNode(value = Token(BooleanLiteral, "false"))
)

## Expression Assignments

### Test: arithmetic_addition_assignment
INPUT:
result = 10 + 5;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = LiteralNode(value = Token(NumberLiteral, "10")),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(NumberLiteral, "5"))
    )
)

### Test: arithmetic_multiplication_assignment
INPUT:
area = width * height;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "area"), name = "area"),
    expression = BinaryOperationNode(
        left = IdentifierNode(value = Token(Identifier, "width"), name = "width"),
        operator = Token(Multiply, "*"),
        right = IdentifierNode(value = Token(Identifier, "height"), name = "height")
    )
)

### Test: string_concatenation_assignment
INPUT:
fullName = firstName + " " + lastName;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "fullName"), name = "fullName"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "firstName"), name = "firstName"),
            operator = Token(Plus, "+"),
            right = LiteralNode(value = Token(StringLiteral, "\" \""))
        ),
        operator = Token(Plus, "+"),
        right = IdentifierNode(value = Token(Identifier, "lastName"), name = "lastName")
    )
)

### Test: complex_arithmetic_expression
INPUT:
result = a + b * c - d / e;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "a"), name = "a"),
            operator = Token(Plus, "+"),
            right = BinaryOperationNode(
                left = IdentifierNode(value = Token(Identifier, "b"), name = "b"),
                operator = Token(Multiply, "*"),
                right = IdentifierNode(value = Token(Identifier, "c"), name = "c")
            )
        ),
        operator = Token(Minus, "-"),
        right = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "d"), name = "d"),
            operator = Token(Divide, "/"),
            right = IdentifierNode(value = Token(Identifier, "e"), name = "e")
        )
    )
)

### Test: parenthesized_expression_assignment
INPUT:
result = (a + b) * c;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "result"), name = "result"),
    expression = BinaryOperationNode(
        left = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "a"), name = "a"),
            operator = Token(Plus, "+"),
            right = IdentifierNode(value = Token(Identifier, "b"), name = "b")
        ),
        operator = Token(Multiply, "*"),
        right = IdentifierNode(value = Token(Identifier, "c"), name = "c")
    )
)

## Unary Expressions

### Test: negative_number_assignment
INPUT:
x = -42;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = UnaryOperationNode(
        operator = Token(Minus, "-"),
        operand = LiteralNode(value = Token(NumberLiteral, "42"))
    )
)

### Test: positive_number_assignment
INPUT:
x = +42;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = UnaryOperationNode(
        operator = Token(Plus, "+"),
        operand = LiteralNode(value = Token(NumberLiteral, "42"))
    )
)

## Assignments with Spaces

### Test: assignment_with_spaces
INPUT:
x   =   42   ;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = LiteralNode(value = Token(NumberLiteral, "42"))
)

### Test: assignment_no_spaces
INPUT:
x=42;
EXPECTED_AST:
AssignmentNode(
    identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
    expression = LiteralNode(value = Token(NumberLiteral, "42"))
)

## Error Cases

### Test: missing_semicolon_assignment
INPUT:
x = 42
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(EOF, ""), expected = "';'")

### Test: missing_expression_assignment
INPUT:
x = ;
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(Semicolon, ";"), expected = "expression")

### Test: invalid_left_side_assignment
INPUT:
42 = x;
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: missing_assignment_operator
INPUT:
x 42;
EXPECTED_ERROR:
ParseError.NoValidParser
