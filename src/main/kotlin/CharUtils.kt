fun Char.isWhitespaceOrNewLine() = isWhitespace() or isNewLine()

fun Char.isNewLine() = this == '\n'

fun Char.isQuote() = this == '\''

fun Char.isBracket() = when(this) {
    '[', ']' -> true
    else -> false
}

fun Char.isBrace() = when(this) {
    '{', '}' -> true
    else -> false
}

fun Char.isDot() = this == '.'