package commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import progress.ConsoleProgressReporter
import progress.ProgressReporter
import java.io.File

/**
 * Base class for all PrintScript CLI commands.
 * Provides common functionality like version handling and file validation.
 */
abstract class BaseCommand(
    name: String,
    help: String,
) : CliktCommand(name = name, help = help) {
    protected val version: String by option(
        "--version",
        "-v",
        help = "PrintScript version (1.0 or 1.1)",
    ).default("1.1")

    protected val progressReporter: ProgressReporter = ConsoleProgressReporter()

    protected fun validateVersion() {
        require(version in listOf("1.0", "1.1")) {
            "Unsupported version: $version. Supported versions: 1.0, 1.1"
        }
    }

    protected fun validateFile(filePath: String): File {
        val file = File(filePath)
        require(file.exists()) { "File not found: $filePath" }
        require(file.canRead()) { "Cannot read file: $filePath" }
        return file
    }

    protected fun validateConfigFile(configPath: String?): File? {
        if (configPath == null) return null
        val file = File(configPath)
        require(file.exists()) { "Configuration file not found: $configPath" }
        return file
    }

    protected fun handleError(error: Throwable) {
        progressReporter.reportError(error.message ?: "Unknown error occurred")
        System.exit(1)
    }

    protected fun reportSuccess(message: String) {
        progressReporter.reportSuccess(message)
    }
}
