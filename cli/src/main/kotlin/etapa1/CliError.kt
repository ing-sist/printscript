package etapa1

open class CliError(message: String) : Exception(message)

class CliFileError(filePath: String) : CliError("\"File not found: $filePath\"")

class CliVersionError(version: Int) : CliError("Unsupported version: $version")