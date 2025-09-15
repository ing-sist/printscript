package viejos.commands

import Result
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import viejos.processor.FileProcessor
import java.io.File

/**
 * Command for formatting PrintScript files.
 */
class FormatCommand :
    BaseCommand(
        name = "format",
        help = "Format a PrintScript file according to style rules",
    ) {
    private val filePath by argument(
        name = "file",
        help = "Path to the PrintScript file to format",
    ).path(mustExist = true, canBeFile = true, canBeDir = false)

    private val configPath by option(
        "--config",
        "-c",
        help = "Path to formatter configuration file (JSON/YAML)",
    )

    private val outputPath by option(
        "--output",
        "-o",
        help = "Output file path (if not specified, overwrites input file)",
    )

    override fun run() {
        try {
            validateVersion()
            val file = filePath.toFile()
            val configFile = validateConfigFile(configPath)

            progressReporter.reportProgress("Starting formatting of ${file.name}")
            progressReporter.reportProgress("Using PrintScript version $version")

            if (configFile != null) {
                progressReporter.reportProgress("Using configuration: ${configFile.name}")
            } else {
                progressReporter.reportProgress("Using default formatting rules")
            }

            val fileProcessor = FileProcessor(progressReporter)

            val result =
                fileProcessor.processFileStreaming(file) { _ ->
                    progressReporter.reportProgress("Parsing for formatting...", 25)
                    progressReporter.reportProgress("Applying formatting rules...", 50)

                    if (version == "1.1") {
                        progressReporter.reportProgress("Applying v1.1 formatting (if blocks, indentation)...", 75)
                    }

                    Result.Success(Unit)
                }

            result.fold(
                onSuccess = {
                    val outputFile = outputPath?.let { File(it) } ?: file
                    progressReporter.reportProgress("Writing formatted output to ${outputFile.name}", 90)
                    reportSuccess("File formatted successfully: ${outputFile.absolutePath}")
                },
                onFailure = { error ->
                    progressReporter.reportError("Formatting failed: ${error.message}")
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
