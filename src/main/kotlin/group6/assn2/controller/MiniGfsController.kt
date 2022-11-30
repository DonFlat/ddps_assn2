package group6.assn2.controller

import group6.assn2.service.MiniGfsClients
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MiniGfsController @Autowired constructor(val miniGfsClients: MiniGfsClients) {

    @Value("nodeSetting.master")
    lateinit var masterNode: String

    @Value("nodeSetting.isMaster")
    lateinit var isMaster: String

    @Value("nodeSetting.nodeId")
    lateinit var nodeId: String

    companion object {
        private val log = LoggerFactory.getLogger(MiniGfsController::class.java)
        private val workerMembership = mutableSetOf<String>()
    }

    @GetMapping("/")
    fun hello(): String {
        log.info("Hit Here!")
        return "I'm ok!\n"
    }

    @GetMapping("/call-client")
    fun callClient() {
        log.info("call client")
        val status = miniGfsClients.getStatus()
        log.info("Called, status: $status")
    }

    @PostMapping("/register-worker/{nodeId}")
    fun registerWorker(@PathVariable nodeId: String): Boolean {
        log.info("Received worker registration request: $nodeId")
        return when (workerMembership.contains(nodeId)) {
            true -> {
                log.info("$nodeId has been registered")
                true
            } false -> {
                log.info("$nodeId not registered, now register")
                workerMembership.add(nodeId)
                log.info("$nodeId successfully registered")
                true
            }
        }
    }

    @Scheduled(fixedRate = 5000)
    fun checkMemberShip() {
        log.info("Checking if registered in master")
        when (miniGfsClients.checkWorkerMemberShip(nodeId)) {
            true -> log.info("Registered!")
            false -> {
                log.info("Retry registration")
                val retryResult = miniGfsClients.checkWorkerMemberShip(nodeId)
                log.info("Retried results: $retryResult")
            }
        }
    }
}