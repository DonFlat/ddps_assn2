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
import utils.Metadata.metadata
import utils.Metadata.workerMembership
import java.io.File
import java.io.FilenameFilter
import java.net.URI
import java.time.Instant

@RestController
class MiniGfsController @Autowired constructor(val miniGfsClients: MiniGfsClients) {

    @Value("\${nodeSetting.master}")
    lateinit var masterNode: String

    @Value("\${nodeSetting.nodeId}")
    lateinit var nodeId: String

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
                log.info("$nodeId has been registered")
                true
            } false -> {
                workerMembership.add(nodeId)
                log.info("$nodeId successfully registered")
                true
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    fun maintainMembership() {
        if (nodeId == masterNode) {
            for (worker in workerMembership) {
                try {
                    miniGfsClients.checkAlive(URI.create("http://$worker:2206"))
                    log.info("worker $worker is alive")
                } catch (e: Exception) {
                    log.error("worker $worker was dead", e)
                }
            }
        } else {
            try {
                when (miniGfsClients.checkWorkerMemberShip(URI.create("http://$masterNode:2206"), nodeId)) {
                    true -> log.info("$nodeId registered!")
                    false -> {
                        log.info("$nodeId retry registration")
                        val retryResult = miniGfsClients.checkWorkerMemberShip(URI.create("http://$masterNode:2206"), nodeId)
                        log.info("$nodeId retried results: $retryResult")
                    }
                }
            } catch (e: Exception) {
                log.error("Unable to register at master $masterNode")
            }
        }
    }

    @GetMapping("/file/{fileName}/metadata")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun getFileMetaData(@PathVariable("fileName") fileName: String): MutableList<String> {
        if (metadata[fileName] == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "not such file")
        } else {
            val replicaList = mutableListOf<String>()
            for (s in metadata[fileName]!!) {
                replicaList.add(s)
            }
            return replicaList
        }
    }

    @GetMapping("/file/{fileName}/content")
    fun getFileContent(@PathVariable("fileName") fileName: String): String {
        log.info("Find $fileName")
        val matchedFiles = File(".").listFiles(WildcardFileFilter("${fileName}*") as FilenameFilter)
        return matchedFiles[0].name
    }

    @PostMapping("/file/{fileName}/content/{replicateNumber}")
    fun writeFile(@PathVariable("fileName") fileName: String, @PathVariable("replicateNumber") replicateNumber: Int, @RequestBody fileContent: String) {
        for (i in 1..replicateNumber) {
            File("${fileName}-${Instant.now()}").writeText(fileContent)
        }
    }

    /**
     * The master's endpoint
     * return a list of node: [lease, secondary replica 1, secondary replica 2]
     */
    @GetMapping("/{fileName}/lease")
    fun getLeaseInfo(@PathVariable("fileName") fileName: String): MutableList<String> {
        val availableChunkServers = mutableSetOf<String>().apply {
            addAll(workerMembership)
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
}