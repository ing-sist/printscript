package runtime.providers

interface EnvProvider {
    fun getEnvVariable(name: String): String?
}

class SystemEnvProvider : EnvProvider {
    override fun getEnvVariable(name: String): String? = System.getenv(name)
}

class MapEnvProvider(
    private val variables: Map<String, String>,
) : EnvProvider {
    override fun getEnvVariable(name: String): String? = variables[name]
}
