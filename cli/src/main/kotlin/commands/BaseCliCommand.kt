import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import language.errors.InterpreterException
import progress.ConsoleProgressReporter
import java.io.IOException

/**
 * A base command for the CLI that handles common logic:
 * 1. Creation of the progress reporter (the one that uses '\r').
 * 2. Centralized error handling (try-catch).
 * 3. Version management.
 */
abstract class BaseCliCommand(
    name: String,
    help: String,
) : CliktCommand(name = name, help = help) {
    protected val version by option("-v", "--version", help = "PrintScript version").default("1.1")

    /**
     * Use the advanced progress reporter.
     */
    protected val reporter = ConsoleProgressReporter()

    /**
     * Final 'run' method that wraps the command logic.
     * Child commands must implement 'executeLogic'.
     */
    final override fun run() {
        try {
            // Call the specific logic of the child command
            executeLogic()
        } catch (e: LexerException) {
            handleError(e)
        } catch (e: IllegalStateException) {
            handleError(e)
        } catch (e: InterpreterException) {
            handleError(e)
        } catch (e: IOException) {
            handleError(e)
        } catch (e: IllegalArgumentException) {
            handleError(e)
        }
    }

    /**
     * Child commands must implement this function with their main logic.
     */
    protected abstract fun executeLogic()

    /**
     * Shows the error using the reporter (which clears the progress line)
     * and exits with an error code.
     */
    protected fun handleError(e: Throwable) {
        val message =
            when (e) {
                is LexerException -> "Lexer Error: ${e.message}"
                is IllegalStateException -> "Parse Error: ${e.message}"
                is InterpreterException -> "Runtime Error: ${e.message}"
                is IOException -> "IO Error: ${e.message}"
                is IllegalArgumentException -> "Validation Error: ${e.message}"
                else -> "Error: ${e.message ?: "Unknown error"}"
            }
        // reportError already clears the progress line
        reporter.reportError(message)
        // Exit the program with error code
        throw ProgramResult(1)
    }
}
