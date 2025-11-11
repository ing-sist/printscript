## Configurar Git Hooks (una sola vez por clon)

Nuestros hooks corren **Spotless**, **Detekt** y **build** antes de cada commit.
Si algo falla, el commit se bloquea.

### macOS / Linux
```bash
./scripts/bootstrap.sh 
```
### Windows
```
sh scripts/bootstrap.sh
```
o
```
bash scripts/bootstrap.sh
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
# Formatear y guardar en archivo
./gradlew run --args="format src/main/resources/examples/unformatted.ps --style src/main/resources/config/format-style-compact.json --output formatted.ps"
```