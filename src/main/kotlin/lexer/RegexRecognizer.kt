package lexer

import lexer.TokenType.*
import java.util.regex.Pattern

interface Recognizer {
    fun number(): (String) -> Pair<Boolean, String>
    fun identifier(): (String) -> Pair<Boolean, String>
    fun keyword(): (String) -> Pair<Boolean, String>
    fun string():  (String) -> Pair<Boolean, String>
}

object RegexRecognizer: Recognizer {

    private const val NUMBER_REGEX = "^\\d+"
    private const val IDENTIFIER_REGEX = "^[a-zA-Z]([a-zA-Z]|\\d|_)*"
    private const val STRING_REGEX = "^\'.*?\'"

    override fun number(): (String) -> Pair<Boolean, String> = {
        recognize(NUMBER_REGEX, it)
    }

    override fun identifier(): (String) -> Pair<Boolean, String> = {
        recognize(IDENTIFIER_REGEX, it)
    }

    override fun keyword(): (String) -> Pair<Boolean, String> {
        val regex = "^($Loop|$It)"
        return { recognize(regex, it) }
    }

    override fun string():  (String) -> Pair<Boolean, String> = {
        recognize(STRING_REGEX, it)
    }

    private fun recognize(regex: String, input: String): Pair<Boolean, String> {
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(input)

        val isMatch = matcher.find()
        val matchedGroup = if (isMatch) matcher.group() else input

        return isMatch to matchedGroup
    }

}