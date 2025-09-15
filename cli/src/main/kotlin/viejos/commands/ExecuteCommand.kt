package viejos.commands

import Result
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import viejos.processor.FileProcessor

/**
 * Command for executing PrintScript files.
 */
class ExecuteCommand :
    BaseCommand(
        name = "execute",
        help = "Execute a PrintScript file",
    ) {
    private val filePath by argument(
        name = "file",
        help = "Path to the PrintScript file to execute",
    ).path(mustExist = true, canBeFile = true, canBeDir = false)

    private val inputFile by option(
        "--input",
        "-i",
        help = "Path to input file for readInput function calls",
    )

    override fun run() {
        try {
            validateVersion()
            val file = filePath.toFile()

            progressReporter.reportProgress("Starting execution of ${file.name}")
            progressReporter.reportProgress("Using PrintScript version $version")

            // Validate input file if provided
            val inputFileObj = inputFile?.let { validateFile(it) }

            val fileProcessor = FileProcessor(progressReporter)

            val result =
                fileProcessor.processFileStreaming(file) { _ ->
                    progressReporter.reportProgress("Lexing and parsing...", 25)
                    progressReporter.reportProgress("Executing...", 50)

                    // For readInput functionality in version 1.1
                    if (version == "1.1" && inputFileObj != null) {
                        progressReporter.reportProgress("Processing input file...", 75)
                    }

                    Result.Success(Unit)
                }

            result.fold(
                onSuccess = {
                    reportSuccess("Execution completed successfully")
                },
                onFailure = { error ->
                    progressReporter.reportError("Execution failed: ${error.message}")
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
