package lexer

class Token(
    val type: TokenType,
    val line: Int,
    val column: Int,
    value: String? = null
) {
    private val _value: String? = value
    val value: String
        get() = _value ?: type.literal

    override fun toString() = value
}