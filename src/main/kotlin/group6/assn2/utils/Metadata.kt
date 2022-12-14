package group6.assn2.utils

object Metadata {
    val workerMembership = mutableMapOf<String, Boolean>()
    /*
       1 elem in deque is primary
       Where the file is
     */
    val metadata = mutableMapOf<String, ArrayDeque<String>>()
}
