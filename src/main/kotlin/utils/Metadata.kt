package utils

object Metadata {
    val workerMembership = mutableSetOf<String>()
    /*
       1 elem in deque is primary
       Where the file is
     */
    val metadata = mutableMapOf<String, ArrayDeque<String>>()
}
