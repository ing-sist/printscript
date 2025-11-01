# PrintScript CLI - Archivos de Recursos

Este directorio contiene archivos de ejemplo y configuración para probar el CLI de PrintScript.

## Estructura

```
cli/src/main/resources/
├── examples/           # Programas de ejemplo
│   ├── hello.ps       # Programa simple "Hello World"
│   ├── calculator.ps  # Operaciones matemáticas
│   ├── nested-if.ps   # Ejemplo de if anidados (v1.1)
│   ├── if-else.ps     # Ejemplo de if-else
│   ├── bad-naming.ps  # Variables con mal naming (snake_case)
│   ├── good-naming.ps # Variables con buen naming (camelCase)
│   └── unformatted.ps # Código sin formatear
└── config/            # Archivos de configuración
    ├── analyzer-camel.json          # Reglas analyzer: camelCase
    ├── analyzer-snake.json          # Reglas analyzer: snake_case
    ├── format-style-compact.json    # Estilo compacto
    └── format-style-spacious.json   # Estilo espacioso
```

## Ejemplos de Uso

### 1. Validar Sintaxis
```bash
# Validar con versión 1.0
./gradlew run --args="validate src/main/resources/examples/hello.ps --version 1.0"

# Validar con versión 1.1
./gradlew run --args="validate src/main/resources/examples/nested-if.ps --version 1.1"
```

### 2. Ejecutar Programas
```bash
# Ejecutar programa simple
./gradlew run --args="execute src/main/resources/examples/hello.ps"

# Ejecutar con if anidados (requiere v1.1)
./gradlew run --args="execute src/main/resources/examples/nested-if.ps --version 1.1"

# Ejecutar calculadora
./gradlew run --args="execute src/main/resources/examples/calculator.ps"

# Ejecutar con readInput (solicita entrada del usuario)
./gradlew run --args="execute src/main/resources/examples/read-input.ps"

# Ejecutar con readEnv (lee variables de entorno)
./gradlew run --args="execute src/main/resources/examples/read-env.ps"

# Ejecutar con input mixto
./gradlew run --args="execute src/main/resources/examples/mixed-input.ps"

# Ejecutar con expresiones de input
./gradlew run --args="execute src/main/resources/examples/input-expressions.ps"
```

### 3. Analizar Código
```bash
# Analizar con reglas camelCase
./gradlew run --args="analyze src/main/resources/examples/bad-naming.ps --rules src/main/resources/config/analyzer-camel.json"

# Analizar con reglas snake_case
./gradlew run --args="analyze src/main/resources/examples/good-naming.ps --rules src/main/resources/config/analyzer-snake.json"
```

### 4. Formatear Código
```bash
# Formatear con estilo compacto
./gradlew run --args="format src/main/resources/examples/unformatted.ps --style src/main/resources/config/format-style-compact.json"

# Formatear con estilo espacioso
./gradlew run --args="format src/main/resources/examples/unformatted.ps --style src/main/resources/config/format-style-spacious.json"

# Formatear y guardar en archivo
./gradlew run --args="format src/main/resources/examples/unformatted.ps --style src/main/resources/config/format-style-compact.json --output formatted.ps"
```

## Archivos de Configuración

### Analyzer Configs

**analyzer-camel.json** - Valida que los identificadores usen camelCase
```json
{
  "identifier-naming": "camel case"
}
```

**analyzer-snake.json** - Valida que los identificadores usen snake_case
```json
{
  "identifier-naming": "snake case"
}
```

### Format Style Configs

**format-style-compact.json** - Estilo compacto (menos espacios)
```json
{
  "space-before-colon": false,
  "space-after-colon": true,
  "space-around-equals": true,
  "newline-before-println": 1
}
```

**format-style-spacious.json** - Estilo espacioso (más espacios)
```json
{
  "space-before-colon": true,
  "space-after-colon": true,
  "space-around-equals": true,
  "newline-before-println": 2
}
```

## Notas

- Los archivos `.ps` son programas PrintScript
- Los archivos de configuración `.json` se usan para analyzer y formatter
- La versión 1.1 incluye soporte para `if/else` statements
- La versión 1.0 solo soporta declaraciones, asignaciones y funciones básicas
