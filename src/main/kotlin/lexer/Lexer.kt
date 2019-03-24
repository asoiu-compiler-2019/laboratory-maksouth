package lexer

import isBrace
import isBracket
import isNewLine
import isQuote
import isWhitespaceOrNewLine

class Lexer(private val input: String) {
    private var currentParsePosition = 0
    private var currentParseLine = 0
    private var currentParseColumn = 0

    private val recognizerFactory: Recognizer = RegexRecognizer

    fun allTokens(): List<Token> {
        val tokens = mutableListOf<Token>()

        var token: Token
        do {
            token = nextToken()
            tokens += token
        } while (token.type != TokenType.EndOfInput
            && token.type != TokenType.Unrecognized
        )

        return tokens
    }

    fun nextToken(): Token {
        skipWhiteSpacesAndNewLines()
        return if (currentParsePosition >= input.length)
            Token(TokenType.EndOfInput, currentParseLine, currentParseColumn)
        else recognizeLexem(input[currentParsePosition])
    }

    private fun skipWhiteSpacesAndNewLines() {
        while (currentParsePosition < input.length &&
                input[currentParsePosition].isWhitespaceOrNewLine()) {

            if (input[currentParsePosition].isNewLine()) {
                currentParseLine++
                currentParseColumn = 0
            } else currentParseColumn++

            currentParsePosition++
        }
    }

    private fun recognizeLexem(char: Char): Token = when {
        char.isQuote() -> recognizeString()
        char.isLetter() -> recognizeCharSequence()
        char.isDigit() -> recognizeNumber()
        char.isBrace() -> recognizeBrace()
        char.isBracket() -> recognizeBracket()
        else -> recognizeSpecialSymbol()
    }

    fun recognizeString(): Token {
        val token = recognizeLanguageWord(
            recognizerFactory.string(),
            { TokenType.StringValue },
            default = unknownToken()
        )!!

        val stringWithoutQuotes = token.value.replace("\'", "")

        return Token(
            type = token.type,
            line = token.line,
            column = token.column,
            value = stringWithoutQuotes
        )
    }

    private fun recognizeCharSequence(): Token =
            recognizeKeyword() ?:
            recognizeIdentifier() ?:
            unknownToken()

    fun recognizeKeyword(): Token? = recognizeLanguageWord(
        recognizerFactory.keyword(),
        ::getKeywordType
    )

    fun recognizeIdentifier(): Token? = recognizeLanguageWord(
        recognizerFactory.identifier(),
        { TokenType.Identifier }
    )

    fun recognizeNumber(): Token = recognizeLanguageWord(
        recognizerFactory.number(),
        { TokenType.Integer },
        default = unknownToken()
    )!!

    private fun recognizeLanguageWord(
        recognizer: (String) -> Pair<Boolean, String>,
        typeResolver: (String) -> TokenType,
        default: Token? = null
    ): Token? {
        val (isKeyword, keyword) = recognizer(input.substring(currentParsePosition))

        if (!isKeyword) return default

        val type = typeResolver(keyword)
        return sequenceSymbolToken(type, keyword)
    }

    private fun sequenceSymbolToken(type: TokenType, value: String = type.literal): Token {
        val column = currentParseColumn
        currentParsePosition += value.length
        currentParseColumn += value.length
        return Token(type, currentParseLine, column, value)
    }

    private fun recognizeBracket(): Token = with(input[currentParsePosition]) {
        val type = getBracketType(this)
        oneSymbolToken(type)
    }

    private fun recognizeBrace(): Token = with(input[currentParsePosition]) {
        val type = getBracketType(this)
        oneSymbolToken(type)
    }

    private fun recognizeSpecialSymbol(): Token = with(input[currentParsePosition]) {
        val tokenType = getSpecialSymbolType(this)
        oneSymbolToken(tokenType)
    }

    private fun oneSymbolToken(type: TokenType): Token {
        val column = currentParseColumn
        currentParsePosition++
        currentParseColumn++
        return Token(type, currentParseLine, column)
    }

    private fun unknownToken(): Token = Token(
        TokenType.Unrecognized,
        currentParseLine,
        currentParseColumn
    )
}