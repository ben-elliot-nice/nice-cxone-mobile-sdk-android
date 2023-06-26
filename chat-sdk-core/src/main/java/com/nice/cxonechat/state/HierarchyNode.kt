package com.nice.cxonechat.state

import com.nice.cxonechat.Public

/**
 * Node definition for general data tree.
 */
@Public
interface HierarchyNode<T> {
    /**
     * Unique identifier of this node, internal value posted to server.
     *
     * @note the name of a particular node/label is not guaranteed to be preserved
     * and should not be locally stored or maintained.
     */
    val nodeId: T

    /**
     * Label for the node, user visible value.
     */
    val label: String

    /**
     * Immutable list of child nodes, if it is empty then this node is a leaf node.
     */
    val children: Sequence<HierarchyNode<T>>

    /**
     * Utility method for checking if this node has any children.
     * Only leaf nodes are valid selectable choices.
     *
     * True if this node [children] list is empty, otherwise false.
     */
    val isLeaf: Boolean
}

/**
 * Search the hierarchy for the node with the given [nodeId].
 *
 * @param T type of payload contained in [HierarchyNode]
 * @receiver [HierarchyNode] to search.
 * @param nodeId [HierarchyNode] to seek and return.
 * @return Matching [HierarchyNode] or null if the node is not found.
 */
@Public
fun <T> HierarchyNode<T>.lookup(nodeId: T): HierarchyNode<T>? =
    if (this.nodeId == nodeId) {
        this
    } else {
        children.lookup(nodeId)
    }

/**
 * Recursively search a list of hierarchy nodes for the node with the given [nodeId].
 *
 * @param T type of payload contained in [HierarchyNode]
 * @receiver Iterable of [HierarchyNode] to search.
 * @param nodeId [HierarchyNode] to seek and return.
 * @return Matching [HierarchyNode] or null if the node is not found.
 */
@Public
fun <T> Sequence<HierarchyNode<T>>.lookup(nodeId: T) =
    firstNotNullOfOrNull { it.lookup(nodeId) }
