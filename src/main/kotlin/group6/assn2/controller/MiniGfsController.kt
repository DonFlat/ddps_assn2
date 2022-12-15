package group6.assn2.controller

import group6.assn2.service.MiniGfsClients
import org.apache.commons.io.filefilter.WildcardFileFilter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import group6.assn2.utils.Metadata.metadata
import group6.assn2.utils.Metadata.workerMembership
import java.io.File
import java.io.FilenameFilter
import java.net.URI
import java.time.Instant

@RestController
class MiniGfsController @Autowired constructor(val miniGfsClients: MiniGfsClients) {

    @Value("\${nodeSetting.master}")
    lateinit var masterNode: String

    @Value("\${nodeSetting.nodeId}")
    lateinit var myNodeId: String

    companion object {
        private val log = LoggerFactory.getLogger(MiniGfsController::class.java)
    }

    @GetMapping("/alive")
    fun checkConfirmation(): String {
        return "up"
    }

    @PostMapping("/register-worker/{nodeId}")
    fun registerWorker(@PathVariable nodeId: String): Boolean {
        return when (workerMembership.contains(nodeId)) {
            true -> {
//                log.info("$nodeId has been registered")
                true
            } false -> {
                workerMembership[nodeId] = true
                log.info("$nodeId successfully registered")
                true
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    fun maintainMembership() {
        if (myNodeId == masterNode) {
            detectAliveChunkServer()
        } else {
            keepRegisteredAtMaster()
        }
    }

    @GetMapping("/file/{fileName}/metadata")
    fun getFileMetaData(@PathVariable("fileName") fileName: String): MutableList<String> {
        log.info("Read metadata of file: $fileName")
        if (metadata[fileName] == null) {
            log.info("No recorded metadata: $fileName")
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "not such file")
        } else {
            val replicaList = mutableListOf<String>()
            for (s in metadata[fileName]!!) {
                replicaList.add(s)
            }
            return replicaList
        }
    }

    @PostMapping("/{fileName}/{nodeId}/metadata")
    fun addFileMetaData(@PathVariable("fileName") fileName: String, @PathVariable("nodeId") nodeId: String) {
        log.info("Add metadata at master: $nodeId, $fileName")
        if (metadata[fileName] == null) {
            metadata[fileName] = ArrayDeque()
            metadata[fileName]!!.add(myNodeId)
        } else {
            metadata[fileName]!!.add(myNodeId)
        }
    }

    @GetMapping("/file/{fileName}/content")
    fun getFileContent(@PathVariable("fileName") fileName: String): String {
        log.info("Find $fileName")
        val matchedFiles = File(".").listFiles(WildcardFileFilter("${fileName}*") as FilenameFilter)
        return matchedFiles[0].readText()
    }

    @PostMapping("/file/{fileName}/content/{replicateNumber}")
    fun writeFile(@PathVariable("fileName") fileName: String, @PathVariable("replicateNumber") replicateNumber: Int, @RequestBody fileContent: String) {
        log.info("Write $fileName at $myNodeId")
        for (i in 1..replicateNumber) {
            File("${fileName}-${myNodeId}-${Instant.now().toString().takeLast(6)}").writeText(fileContent)
        }
        miniGfsClients.addFileMetaData(URI.create("http://$masterNode:2206"), fileName, myNodeId)
    }

    /**
     * The master's endpoint
     * return a list of node: [lease, secondary replica 1, secondary replica 2]
     */
    @GetMapping("/{fileName}/lease")
    fun getLeaseInfo(@PathVariable("fileName") fileName: String): MutableList<String> {
        val availableChunkServers = mutableSetOf<String>().apply {
            addAll(workerMembership.keys.filter { workerMembership[it] == true })
        }

        val replicas = metadata[fileName]

        val chunkServersToWrite = mutableListOf<String>()
        if (replicas == null) {
            assignNewChunkServers(availableChunkServers, chunkServersToWrite)
        } else {
            replicas.forEach { chunkServersToWrite.add(it) }
        }
        return chunkServersToWrite
    }

    private fun assignNewChunkServers(
        availableChunkServers: MutableSet<String>,
        chunkServersToWrite: MutableList<String>
    ) {
        // Random assign a primary
        availableChunkServers.random().also {
            availableChunkServers.remove(it)
            chunkServersToWrite.add(it)
        }
        // poll more chunkservers as secondary replicas
        while (availableChunkServers.size > 0 && chunkServersToWrite.size < 3) {
            availableChunkServers.random().also {
                availableChunkServers.remove(it)
                chunkServersToWrite.add(it)
            }
        }
    }

    private fun keepRegisteredAtMaster() {
        try {
            when (miniGfsClients.checkWorkerMemberShip(URI.create("http://$masterNode:2206"), myNodeId)) {
                true -> {
//                    log.info("$myNodeId registered!")
                }
                false -> {
                    log.info("$myNodeId retry registration")
                    val retryResult =
                        miniGfsClients.checkWorkerMemberShip(URI.create("http://$masterNode:2206"), myNodeId)
                    log.info("$myNodeId retried results: $retryResult")
                }
            }
        } catch (e: Exception) {
            log.error("Unable to register at master $masterNode")
        }
    }

    private fun detectAliveChunkServer() {
        for (worker in workerMembership) {
            try {
                miniGfsClients.checkAlive(URI.create("http://${worker.key}:2206"))
//                log.info("worker $worker is alive")
            } catch (e: Exception) {
                worker.setValue(false)
                log.error("worker ${worker.key} was dead", e)
            }
        }
    }
}