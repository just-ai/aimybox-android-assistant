interface Submodule {
    val name: String
    val version: String
    val isPublication: Boolean
}

data class Library(
    override val name: String,
    override val version: String,
    override val isPublication: Boolean
) : Submodule

data class Application(
    override val name: String,
    override val version: String,
    override val isPublication: Boolean
) : Submodule