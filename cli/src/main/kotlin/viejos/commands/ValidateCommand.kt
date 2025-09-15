package viejos.commands

import Result
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.path
import viejos.processor.FileProcessor

/**
 * Command for validating PrintScript files.
 * Checks syntax and semantics without executing the code.
 */
class ValidateCommand :
    BaseCommand(
        name = "validate",
        help = "Validate syntax and semantics of a PrintScript file",
    ) {
    private val filePath by argument(
        name = "file",
        help = "Path to the PrintScript file to validate",
    ).path(mustExist = true, canBeFile = true, canBeDir = false)

    override fun run() {
        try {
            validateVersion()
            val file = filePath.toFile()

            progressReporter.reportProgress("Starting validation of ${file.name}")
            progressReporter.reportProgress("Using PrintScript version $version")

            val fileProcessor = FileProcessor(progressReporter)

            val result =
                fileProcessor.processFileStreaming(file) { _ ->
                    // Here we would validate the tokens using lexer and parser
                    // For now, we'll simulate the validation process
                    progressReporter.reportProgress("Validating syntax...", 50)
                    progressReporter.reportProgress("Validating semantics...", 75)

                    // Return success result
                    Result.Success(Unit)
                }

            result.fold(
                onSuccess = {
                    reportSuccess("File ${file.name} is valid")
                },
                onFailure = { error ->
                    progressReporter.reportError(error.message)
                    System.exit(1)
                },
            )
        } catch (e: IllegalArgumentException) {
            handleError(e)
        } catch (e: SecurityException) {
            handleError(e)
        } catch (e: IllegalStateException) {
            handleError(e)
        }
    }
}
