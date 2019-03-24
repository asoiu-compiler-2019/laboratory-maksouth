package parser

interface Element

data class Node(
    val name: String,
    val attributes: List<Attribute>,
    val elements: List<Element>
): Element

data class Attribute(
    val name: String,
    val value: Value
)

data class Loop(
    val list: ValueList,
    val node: Node
): Element

data class ValueList(
    val elements: List<ListElement>
): ListElement

sealed class Value: Element
interface ListElement

data class StringExpression(
    val value: String
): Value(), ListElement

data class ParameterExpression(
    val accessIndices: List<Int>
): Value()


