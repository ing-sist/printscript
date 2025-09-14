# Function Call Tests - PrintScript 1.0 & 1.1

## Basic Function Calls - PrintScript 1.0

### Test: println_string_literal
INPUT:
println("Hello World");
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(StringLiteral, "\"Hello World\"")),
    isVoid = true
)

### Test: println_number_literal
INPUT:
println(42);
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(NumberLiteral, "42")),
    isVoid = true
)

### Test: println_identifier
INPUT:
println(userName);
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = IdentifierNode(value = Token(Identifier, "userName"), name = "userName"),
    isVoid = true
)

### Test: println_boolean_literal_true
INPUT:
println(true);
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(BooleanLiteral, "true")),
    isVoid = true
)

### Test: println_boolean_literal_false
INPUT:
println(false);
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(BooleanLiteral, "false")),
    isVoid = true
)

## Function Calls with Expressions

### Test: println_arithmetic_expression
INPUT:
println(10 + 5);
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = BinaryOperationNode(
        left = LiteralNode(value = Token(NumberLiteral, "10")),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(NumberLiteral, "5"))
    ),
    isVoid = true
)

### Test: println_string_concatenation
INPUT:
println("Hello " + name);
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = BinaryOperationNode(
        left = LiteralNode(value = Token(StringLiteral, "\"Hello \"")),
        operator = Token(Plus, "+"),
        right = IdentifierNode(value = Token(Identifier, "name"), name = "name")
    ),
    isVoid = true
)

### Test: println_complex_expression
INPUT:
println("Result: " + (a + b) * c);
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = BinaryOperationNode(
        left = LiteralNode(value = Token(StringLiteral, "\"Result: \"")),
        operator = Token(Plus, "+"),
        right = BinaryOperationNode(
            left = BinaryOperationNode(
                left = IdentifierNode(value = Token(Identifier, "a"), name = "a"),
                operator = Token(Plus, "+"),
                right = IdentifierNode(value = Token(Identifier, "b"), name = "b")
            ),
            operator = Token(Multiply, "*"),
            right = IdentifierNode(value = Token(Identifier, "c"), name = "c")
        )
    ),
    isVoid = true
)

## PrintScript 1.1 Functions - readInput

### Test: readInput_string_literal
INPUT:
readInput("Enter your name: ");
EXPECTED_AST:
FunctionCallNode(
    functionName = "readInput",
    content = LiteralNode(value = Token(StringLiteral, "\"Enter your name: \"")),
    isVoid = false
)

### Test: readInput_identifier
INPUT:
readInput(prompt);
EXPECTED_AST:
FunctionCallNode(
    functionName = "readInput",
    content = IdentifierNode(value = Token(Identifier, "prompt"), name = "prompt"),
    isVoid = false
)

### Test: readInput_string_concatenation
INPUT:
readInput("Enter " + fieldName + ": ");
EXPECTED_AST:
FunctionCallNode(
    functionName = "readInput",
    content = BinaryOperationNode(
        left = BinaryOperationNode(
            left = LiteralNode(value = Token(StringLiteral, "\"Enter \"")),
            operator = Token(Plus, "+"),
            right = IdentifierNode(value = Token(Identifier, "fieldName"), name = "fieldName")
        ),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(StringLiteral, "\": \""))
    ),
    isVoid = false
)

## PrintScript 1.1 Functions - readEnv

### Test: readEnv_string_literal
INPUT:
readEnv("HOME");
EXPECTED_AST:
FunctionCallNode(
    functionName = "readEnv",
    content = LiteralNode(value = Token(StringLiteral, "\"HOME\"")),
    isVoid = false
)

### Test: readEnv_identifier
INPUT:
readEnv(envVarName);
EXPECTED_AST:
FunctionCallNode(
    functionName = "readEnv",
    content = IdentifierNode(value = Token(Identifier, "envVarName"), name = "envVarName"),
    isVoid = false
)

### Test: readEnv_string_concatenation
INPUT:
readEnv("APP_" + environment);
EXPECTED_AST:
FunctionCallNode(
    functionName = "readEnv",
    content = BinaryOperationNode(
        left = LiteralNode(value = Token(StringLiteral, "\"APP_\"")),
        operator = Token(Plus, "+"),
        right = IdentifierNode(value = Token(Identifier, "environment"), name = "environment")
    ),
    isVoid = false
)

## Function Calls with Spaces

### Test: println_with_spaces
INPUT:
println   (   "Hello"   )   ;
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(StringLiteral, "\"Hello\"")),
    isVoid = true
)

### Test: readInput_no_spaces
INPUT:
readInput("prompt");
EXPECTED_AST:
FunctionCallNode(
    functionName = "readInput",
    content = LiteralNode(value = Token(StringLiteral, "\"prompt\"")),
    isVoid = false
)

## Nested Parentheses

### Test: println_nested_parentheses
INPUT:
println((a + b) * (c - d));
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = BinaryOperationNode(
        left = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "a"), name = "a"),
            operator = Token(Plus, "+"),
            right = IdentifierNode(value = Token(Identifier, "b"), name = "b")
        ),
        operator = Token(Multiply, "*"),
        right = BinaryOperationNode(
            left = IdentifierNode(value = Token(Identifier, "c"), name = "c"),
            operator = Token(Minus, "-"),
            right = IdentifierNode(value = Token(Identifier, "d"), name = "d")
        )
    ),
    isVoid = true
)

## Empty Function Calls

### Test: println_empty_string
INPUT:
println("");
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(StringLiteral, "\"\"")),
    isVoid = true
)

## Error Cases

### Test: missing_semicolon_function_call
INPUT:
println("Hello")
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(EOF, ""), expected = "';'")

### Test: missing_closing_parenthesis
INPUT:
println("Hello";
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(Semicolon, ";"), expected = "')'")

### Test: missing_opening_parenthesis
INPUT:
println "Hello");
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: empty_function_call_parentheses
INPUT:
println();
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(RightParen, ")"), expected = "expression")

### Test: invalid_function_name
INPUT:
invalidFunc("Hello");
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: unmatched_nested_parentheses
INPUT:
println((a + b);
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(Semicolon, ";"), expected = "')'")

## Multiple Quote Types

### Test: println_single_quotes
INPUT:
println('Hello World');
EXPECTED_AST:
FunctionCallNode(
    functionName = "println",
    content = LiteralNode(value = Token(StringLiteral, "'Hello World'")),
    isVoid = true
)

### Test: readInput_mixed_quotes_in_concatenation
INPUT:
readInput("Enter '" + field + "': ");
EXPECTED_AST:
FunctionCallNode(
    functionName = "readInput",
    content = BinaryOperationNode(
        left = BinaryOperationNode(
            left = LiteralNode(value = Token(StringLiteral, "\"Enter '\"")),
            operator = Token(Plus, "+"),
            right = IdentifierNode(value = Token(Identifier, "field"), name = "field")
        ),
        operator = Token(Plus, "+"),
        right = LiteralNode(value = Token(StringLiteral, "\"': \""))
    ),
    isVoid = false
)
