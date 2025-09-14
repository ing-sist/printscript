# If/Conditional Tests - PrintScript 1.1

## Basic If Statements

### Test: simple_if_boolean_identifier
INPUT:
if (isValid) {
    println("Valid");
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "isValid"), name = "isValid"),
    thenBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Valid\"")),
            isVoid = true
        )
    ],
    elseBody = null
)

### Test: simple_if_boolean_literal_true
INPUT:
if (true) {
    println("Always true");
}
EXPECTED_AST:
ConditionalNode(
    condition = LiteralNode(value = Token(BooleanLiteral, "true")),
    thenBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Always true\"")),
            isVoid = true
        )
    ],
    elseBody = null
)

### Test: simple_if_boolean_literal_false
INPUT:
if (false) {
    println("Never executed");
}
EXPECTED_AST:
ConditionalNode(
    condition = LiteralNode(value = Token(BooleanLiteral, "false")),
    thenBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Never executed\"")),
            isVoid = true
        )
    ],
    elseBody = null
)

## If-Else Statements

### Test: if_else_boolean_identifier
INPUT:
if (hasPermission) {
    println("Access granted");
} else {
    println("Access denied");
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "hasPermission"), name = "hasPermission"),
    thenBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Access granted\"")),
            isVoid = true
        )
    ],
    elseBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Access denied\"")),
            isVoid = true
        )
    ]
)

### Test: if_else_boolean_literal
INPUT:
if (true) {
    println("True branch");
} else {
    println("False branch");
}
EXPECTED_AST:
ConditionalNode(
    condition = LiteralNode(value = Token(BooleanLiteral, "true")),
    thenBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"True branch\"")),
            isVoid = true
        )
    ],
    elseBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"False branch\"")),
            isVoid = true
        )
    ]
)

## Multiple Statements in Blocks

### Test: if_multiple_statements_in_then
INPUT:
if (isReady) {
    let status: string = "Ready";
    println(status);
    x = 42;
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "isReady"), name = "isReady"),
    thenBody = [
        DeclarationAssignmentNode(
            declaration = DeclarationNode(
                identifier = IdentifierNode(value = Token(Identifier, "status"), name = "status"),
                type = Token(StringType, "string"),
                isMutable = true
            ),
            value = LiteralNode(value = Token(StringLiteral, "\"Ready\""))
        ),
        FunctionCallNode(
            functionName = "println",
            content = IdentifierNode(value = Token(Identifier, "status"), name = "status"),
            isVoid = true
        ),
        AssignmentNode(
            identifier = IdentifierNode(value = Token(Identifier, "x"), name = "x"),
            expression = LiteralNode(value = Token(NumberLiteral, "42"))
        )
    ],
    elseBody = null
)

### Test: if_else_multiple_statements_both_blocks
INPUT:
if (shouldProcess) {
    const message: string = "Processing";
    println(message);
} else {
    const error: string = "Skipped";
    println(error);
    status = false;
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "shouldProcess"), name = "shouldProcess"),
    thenBody = [
        DeclarationAssignmentNode(
            declaration = DeclarationNode(
                identifier = IdentifierNode(value = Token(Identifier, "message"), name = "message"),
                type = Token(StringType, "string"),
                isMutable = false
            ),
            value = LiteralNode(value = Token(StringLiteral, "\"Processing\""))
        ),
        FunctionCallNode(
            functionName = "println",
            content = IdentifierNode(value = Token(Identifier, "message"), name = "message"),
            isVoid = true
        )
    ],
    elseBody = [
        DeclarationAssignmentNode(
            declaration = DeclarationNode(
                identifier = IdentifierNode(value = Token(Identifier, "error"), name = "error"),
                type = Token(StringType, "string"),
                isMutable = false
            ),
            value = LiteralNode(value = Token(StringLiteral, "\"Skipped\""))
        ),
        FunctionCallNode(
            functionName = "println",
            content = IdentifierNode(value = Token(Identifier, "error"), name = "error"),
            isVoid = true
        ),
        AssignmentNode(
            identifier = IdentifierNode(value = Token(Identifier, "status"), name = "status"),
            expression = LiteralNode(value = Token(BooleanLiteral, "false"))
        )
    ]
)

## Empty Blocks

### Test: if_empty_then_block
INPUT:
if (flag) {
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "flag"), name = "flag"),
    thenBody = [],
    elseBody = null
)

### Test: if_else_empty_blocks
INPUT:
if (condition) {
} else {
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "condition"), name = "condition"),
    thenBody = [],
    elseBody = []
)

## Nested If Statements

### Test: nested_if_in_then_block
INPUT:
if (outer) {
    if (inner) {
        println("Both true");
    }
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "outer"), name = "outer"),
    thenBody = [
        ConditionalNode(
            condition = IdentifierNode(value = Token(Identifier, "inner"), name = "inner"),
            thenBody = [
                FunctionCallNode(
                    functionName = "println",
                    content = LiteralNode(value = Token(StringLiteral, "\"Both true\"")),
                    isVoid = true
                )
            ],
            elseBody = null
        )
    ],
    elseBody = null
)

### Test: nested_if_in_else_block
INPUT:
if (first) {
    println("First is true");
} else {
    if (second) {
        println("Second is true");
    } else {
        println("Both false");
    }
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "first"), name = "first"),
    thenBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"First is true\"")),
            isVoid = true
        )
    ],
    elseBody = [
        ConditionalNode(
            condition = IdentifierNode(value = Token(Identifier, "second"), name = "second"),
            thenBody = [
                FunctionCallNode(
                    functionName = "println",
                    content = LiteralNode(value = Token(StringLiteral, "\"Second is true\"")),
                    isVoid = true
                )
            ],
            elseBody = [
                FunctionCallNode(
                    functionName = "println",
                    content = LiteralNode(value = Token(StringLiteral, "\"Both false\"")),
                    isVoid = true
                )
            ]
        )
    ]
)

## If Statements with Different Statement Types

### Test: if_with_declarations
INPUT:
if (initialize) {
    let count: number = 0;
    const name: string = "Test";
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "initialize"), name = "initialize"),
    thenBody = [
        DeclarationNode(
            identifier = IdentifierNode(value = Token(Identifier, "count"), name = "count"),
            type = Token(NumberType, "number"),
            isMutable = true
        ),
        DeclarationAssignmentNode(
            declaration = DeclarationNode(
                identifier = IdentifierNode(value = Token(Identifier, "name"), name = "name"),
                type = Token(StringType, "string"),
                isMutable = false
            ),
            value = LiteralNode(value = Token(StringLiteral, "\"Test\""))
        )
    ],
    elseBody = null
)

### Test: if_with_function_calls
INPUT:
if (debug) {
    println("Debug mode");
    readInput("Continue? ");
    readEnv("LOG_LEVEL");
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "debug"), name = "debug"),
    thenBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Debug mode\"")),
            isVoid = true
        ),
        FunctionCallNode(
            functionName = "readInput",
            content = LiteralNode(value = Token(StringLiteral, "\"Continue? \"")),
            isVoid = false
        ),
        FunctionCallNode(
            functionName = "readEnv",
            content = LiteralNode(value = Token(StringLiteral, "\"LOG_LEVEL\"")),
            isVoid = false
        )
    ],
    elseBody = null
)

## Whitespace and Formatting Variations

### Test: if_with_extra_spaces
INPUT:
if   (   flag   )   {
    println   (   "Spaced"   )   ;
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "flag"), name = "flag"),
    thenBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Spaced\"")),
            isVoid = true
        )
    ],
    elseBody = null
)

### Test: if_else_compact_format
INPUT:
if(flag){println("Compact");}else{println("Also compact");}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "flag"), name = "flag"),
    thenBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Compact\"")),
            isVoid = true
        )
    ],
    elseBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Also compact\"")),
            isVoid = true
        )
    ]
)

## Error Cases

### Test: if_missing_condition
INPUT:
if () {
    println("Empty condition");
}
EXPECTED_ERROR:
ParseError.InvalidSyntax(tokens = [], message = "If condition must be a boolean identifier or boolean literal (true/false)")

### Test: if_invalid_condition_expression
INPUT:
if (x + y) {
    println("Invalid condition");
}
EXPECTED_ERROR:
ParseError.InvalidSyntax(tokens = [Token(Identifier, "x")], message = "If condition must be a boolean identifier or boolean literal (true/false)")

### Test: if_number_condition
INPUT:
if (42) {
    println("Number condition");
}
EXPECTED_ERROR:
ParseError.InvalidSyntax(tokens = [Token(NumberLiteral, "42")], message = "If condition must be a boolean identifier or boolean literal (true/false)")

### Test: if_string_condition
INPUT:
if ("true") {
    println("String condition");
}
EXPECTED_ERROR:
ParseError.InvalidSyntax(tokens = [Token(StringLiteral, "\"true\"")], message = "If condition must be a boolean identifier or boolean literal (true/false)")

### Test: if_missing_opening_parenthesis
INPUT:
if flag) {
    println("Missing opening paren");
}
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(Identifier, "flag"), expected = "'('")

### Test: if_missing_closing_parenthesis
INPUT:
if (flag {
    println("Missing closing paren");
}
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(LeftBrace, "{"), expected = "')'")

### Test: if_missing_opening_brace
INPUT:
if (flag)
    println("Missing opening brace");
}
EXPECTED_ERROR:
ParseError.InvalidSyntax(tokens = [Token(Identifier, "println")], message = "Expected '{' to start if block")

### Test: if_missing_closing_brace
INPUT:
if (flag) {
    println("Missing closing brace");
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(EOF, ""), expected = "'}'")

### Test: else_without_if
INPUT:
else {
    println("Orphaned else");
}
EXPECTED_ERROR:
ParseError.NoValidParser

### Test: else_missing_opening_brace
INPUT:
if (flag) {
    println("Valid if");
} else
    println("Missing else brace");
}
EXPECTED_ERROR:
ParseError.InvalidSyntax(tokens = [Token(Identifier, "println")], message = "Expected '{' to start else block")

### Test: nested_if_missing_closing_brace
INPUT:
if (outer) {
    if (inner) {
        println("Nested");
    }
EXPECTED_ERROR:
ParseError.UnexpectedToken(token = Token(EOF, ""), expected = "'}'")

## Complex Nested Scenarios

### Test: deeply_nested_if_statements
INPUT:
if (level1) {
    if (level2) {
        if (level3) {
            println("Deep nesting");
        } else {
            println("Level 3 false");
        }
    } else {
        println("Level 2 false");
    }
} else {
    println("Level 1 false");
}
EXPECTED_AST:
ConditionalNode(
    condition = IdentifierNode(value = Token(Identifier, "level1"), name = "level1"),
    thenBody = [
        ConditionalNode(
            condition = IdentifierNode(value = Token(Identifier, "level2"), name = "level2"),
            thenBody = [
                ConditionalNode(
                    condition = IdentifierNode(value = Token(Identifier, "level3"), name = "level3"),
                    thenBody = [
                        FunctionCallNode(
                            functionName = "println",
                            content = LiteralNode(value = Token(StringLiteral, "\"Deep nesting\"")),
                            isVoid = true
                        )
                    ],
                    elseBody = [
                        FunctionCallNode(
                            functionName = "println",
                            content = LiteralNode(value = Token(StringLiteral, "\"Level 3 false\"")),
                            isVoid = true
                        )
                    ]
                )
            ],
            elseBody = [
                FunctionCallNode(
                    functionName = "println",
                    content = LiteralNode(value = Token(StringLiteral, "\"Level 2 false\"")),
                    isVoid = true
                )
            ]
        )
    ],
    elseBody = [
        FunctionCallNode(
            functionName = "println",
            content = LiteralNode(value = Token(StringLiteral, "\"Level 1 false\"")),
            isVoid = true
        )
    ]
)
