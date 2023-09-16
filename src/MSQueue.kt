import java.util.concurrent.atomic.AtomicReference

class MSQueue<E> : Queue<E> {
    private val head: AtomicReference<Node<E>>
    private val tail: AtomicReference<Node<E>>

    init {
        val firstNode = Node<E>(null)
        head = AtomicReference(firstNode)
        tail = AtomicReference(firstNode)
    }

    override fun enqueue(element: E) {
        val node: Node<E> = Node(element)
        var curTail : Node<E>
        while (true) {
            curTail = tail.get()
            val next = curTail.next.get()
            if (curTail === tail.get()) {
                if (next == null) {
                    if (curTail.next.compareAndSet(null, node)) {
                        break
                    }
                }
                else tail.compareAndSet(curTail, next)
            }
        }
        tail.compareAndSet(curTail, node)
    }

    override fun dequeue(): E? {
        var element: E?
        while (true) {
            val curHead = head.get()
            val curTail = tail.get()
            val next = curHead.next.get()
            if (head.get() === curHead) {
                if (curHead === curTail) {
                    if (next == null) {
                        return null
                    }
                    tail.compareAndSet(curTail, next)
                } else {
                    element = next?.element
                    if (head.compareAndSet(curHead, next)) {
                        head.get().element = null
                        return element
                    }
                }
            }
        }
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
