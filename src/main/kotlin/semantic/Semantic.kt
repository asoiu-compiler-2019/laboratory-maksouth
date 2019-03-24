package semantic

import parser.*

sealed class Result {
    companion object {
        fun ok() = Success
        fun bad(message: String) = Failure(message)
    }
}
object Success : Result()
class Failure(val message: String): Result()

data class Scope(
    val parent: Scope?,
    var list: ValueList? = null
)

fun validate(node: Node): Result = validateNode(node)

private fun validateNode(node: Node, parentScope: Scope? = null): Result {
    val scope = Scope(parent = parentScope)

    val attributeValidationResult = validateAttributes(node.attributes, scope)
    if (attributeValidationResult is Failure) return attributeValidationResult

    return validateElements(node.elements, scope)
}

private fun validateAttributes(attributes: List<Attribute>, scope: Scope): Result {
    for (attribute in attributes)
        if (!validateAttribute(attribute, scope))
            return Result.bad("Bad attribute $attribute")
    return Result.ok()
}

private fun validateAttribute(attribute: Attribute, scope: Scope): Boolean = when(attribute.value) {
    is ParameterExpression -> scope.canAccessList()
    else -> true
}

private fun Scope.canAccessList(): Boolean {
    if (list != null) return true
    return parent?.canAccessList() == true
}

private fun validateElements(elements: List<Element>, scope: Scope): Result {
    for (element in elements) {
        val elementResult = validateElement(element, scope)
        if (elementResult is Failure)
            return elementResult
    }

    return Result.ok()
}

private fun validateElement(element: Element, scope: Scope): Result = when(element) {
    is Loop -> {
        scope.list = element.list
        validateNode(element.node, scope)
    }
    is Node -> validateNode(element, scope)
    is ParameterExpression -> validateParameterExpression(scope)
    else -> Result.ok()
}

private fun validateParameterExpression(scope: Scope): Result =
    if (scope.canAccessList()) Result.ok()
    else Result.bad("Cannot access outer list")

