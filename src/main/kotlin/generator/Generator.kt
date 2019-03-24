package generator

import parser.*
import java.lang.IllegalStateException
import java.lang.StringBuilder

data class Scope(
    val parent: Scope? = null,
    var listElement: String? = null
)

const val template = """
@file:JvmName("GenerateXml")
import org.redundent.kotlin.xml.xml

fun main() {
    val xml = BODY

    println(xml)
}
"""

class Generator(private val rootNode: Node) {
    fun generate(): String {
        val xml = generateRootNode(rootNode, Scope())
        return template.replace("BODY", xml)
    }

    fun generateRootNode(node: Node, scope: Scope): String =
        "xml(${string(node.name)}) " + generateNodeBody(node, scope)


    fun generateAttributes(attributes: List<Attribute>, scope: Scope): String = with(StringBuilder()){
        for (attribute in attributes) {
            append(generateAttribute(attribute, scope))
            append("\n")
        }
        toString()
    }

    fun generateAttribute(attribute: Attribute, scope: Scope): String {
        val attributeValue = when(attribute.value) {
            is ParameterExpression -> generateParameterExpression(attribute.value.accessIndices, scope)
            is StringExpression -> string(attribute.value.value)
        }
        val name = string(attribute.name)

        return "attribute($name, $attributeValue)\n"
    }

    private fun generateParameterExpression(
        accessIndices: List<Int>,
        scope: Scope
    ): String {
        var indexes = ""
        for (index in accessIndices)
            indexes += "[$index]"
        return findListElementName(scope) + indexes + ".toString()"
    }

    fun findListElementName(scope: Scope): String =
        if (scope.listElement != null) scope.listElement!!
        else if(scope.parent != null) findListElementName(scope.parent)
        else throw IllegalStateException()

    fun generateElements(elements: List<Element>, scope: Scope): String = with(StringBuilder("")) {
        for (element in elements)
            append(generateElement(element, scope))

        toString()
    }

    fun generateElement(element: Element, scope: Scope): String = when(element) {
        is Node -> generateNode(element, scope)
        is Loop -> generateLoop(element, scope)
        is StringExpression -> generateStringValue(element)
        is ParameterExpression -> generateParameterElementValue(element, scope)
        else -> throw IllegalStateException()
    }

    private fun generateParameterElementValue(element: ParameterExpression, scope: Scope): String =
        "-${generateParameterExpression(element.accessIndices, scope)}\n"

    fun generateNode(node: Node, parentScope: Scope): String {
        val scope = Scope(parentScope)
        return string(node.name) + generateNodeBody(node, scope)
    }

    fun generateNodeBody(node: Node, scope: Scope): String =
        "{\n" +
            generateAttributes(node.attributes, scope).tab() +
            generateElements(node.elements, scope).tab() +
        "}\n"

    fun String.tab() = replace("\n", "\n\t").removeSuffix("\t")

    fun generateLoop(loop: Loop, parentScope: Scope): String = with(StringBuilder()) {
        val listName = newListName()
        val listElementName = newListElementName()

        val scope = Scope(parentScope, listElementName)

        append("val $listName = ${generateList(loop.list)}\n")
        append("for ($listElementName in $listName)\n")
        append(generateNode(loop.node, scope).tab())

        toString()
    }

    var listId = 0
    fun newListName(): String = "list${listId++}"

    var elementId = 0
    fun newListElementName() = "element${elementId++}"

    private fun generateList(list: ValueList): String {
        val delimiter = ", "
        var listBody = ""
        for (element in list.elements) {
            listBody += generateListElement(element)
            listBody += delimiter
        }
        listBody = listBody.removeSuffix(delimiter)

        return "listOf($listBody)"
    }

    private fun generateListElement(element: ListElement): String = when(element) {
        is ValueList -> generateList(element)
        is StringExpression -> string(element.value)
        else -> throw IllegalStateException()
    }

    fun generateStringValue(value: StringExpression) =
            "-${string(value.value)}\n"

    fun string(value: String) = "\"$value\""
}