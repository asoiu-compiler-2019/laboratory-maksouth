package parser

import lexer.Token
import lexer.TokenType
import java.lang.IllegalStateException

class Parser(
    private val tokens: List<Token>
) {
    private var currentIndex = 0

    fun parse(): Node {
        return parseNode()
    }

    private fun parseNode(): Node {
        current(TokenType.OpenBracket, "Node should start with [")

        val name = next(TokenType.Identifier, "Node name should be valid identifier")

        val attributes = checkAttributes()
        val elements = checkElements()

        next(TokenType.CloseBracket, "Node should end with ]")

        return Node(
            name = name.value,
            attributes = attributes,
            elements = elements
        )
    }

    private fun checkAttributes(): List<Attribute> = mutableListOf<Attribute>().apply {
        while (next().type == TokenType.Semicolon) {
            val name = next(TokenType.Identifier, "Attribute name should be valid identifier")
            val value = checkValue()

            add(Attribute(name.value, value))
        }

        moveBack()
    }

    private fun checkValue(): Value = with(next()) {
        return if (type == TokenType.It)
            parseItParameter()
        else parseString()
    }

    private fun checkElements(): List<Element> = mutableListOf<Element>().apply {
        while (!endOfNode())
            add(checkElement())
    }

    private fun endOfNode() =
        seeNext().type == TokenType.CloseBracket || seeNext().type == TokenType.EndOfInput

    private fun checkElement(): Element = when(next().type) {
        TokenType.Loop -> parseLoop()
        TokenType.It -> parseItParameter()
        TokenType.OpenBracket -> parseNode()
        TokenType.StringValue -> parseString()
        else -> returnError("Unknown construction")
    }

    private fun parseLoop(): Loop {
        next()
        val list = parseList()
        next()
        val node = parseNode()

        return Loop(list, node)
    }

    private fun parseList(): ValueList {
        current(TokenType.OpenBrace, "List should start with {")

        val elements = mutableListOf<ListElement>()
        while (!endOfList())
            elements.add(checkListElement())

        next(TokenType.CloseBrace, "List should end with }")
        return ValueList(elements)
    }

    private fun endOfList() =
        seeNext().type == TokenType.CloseBrace || seeNext().type == TokenType.EndOfInput

    private fun checkListElement(): ListElement = when(next().type) {
        TokenType.OpenBrace -> parseList()
        TokenType.StringValue -> parseString()
        else -> returnError("Unknown construction")
    }

    private fun parseItParameter(): ParameterExpression = with(mutableListOf<Int>()){
        while (next().type == TokenType.Dot) {
            val index = next(TokenType.Integer, "Index must be a number")
            add(index.value.toInt())
        }

        moveBack()
        ParameterExpression(this)
    }

    private fun parseString(): StringExpression {
        val string = current(TokenType.StringValue, "Cannot parse string")
        return StringExpression(string.value)
    }

    private fun next(
        type: TokenType,
        errorMessage: String? = null
    ): Token {
        next()
        return current(type, errorMessage)
    }

    private fun current(
        type: TokenType,
        errorMessage: String? = null
    ): Token = current().let {
        if (it.type == type) it
        else returnError(errorMessage)
    }

    private fun returnError(message: String?): Nothing {
        val token = current()
        val errorMessage = "invalid token ${token.value} " +
                "of type ${token.type} " +
                "(${token.line}:${token.column}) " +
                "$message"

        println("Parser: $errorMessage")
        throw IllegalStateException(errorMessage)
    }

    private fun next() = tokens[++currentIndex]

    private fun seeNext() = tokens[currentIndex + 1]

    private fun current() = tokens[currentIndex]

    private fun moveBack() = --currentIndex

}