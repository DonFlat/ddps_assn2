package utils

object Metadata {
    val workerMembership = mutableSetOf<String>()
    val metadata = mutableMapOf<String, ArrayDeque<String>>() // 1 elem in deque is primary
}
