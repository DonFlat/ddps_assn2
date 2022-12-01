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
import java.net.URI

@RestController
class MiniGfsController @Autowired constructor(val miniGfsClients: MiniGfsClients) {

    @Value("\${nodeSetting.master}")
    lateinit var masterNode: String

    @Value("\${nodeSetting.nodeId}")
    lateinit var nodeId: String

    companion object {
        private val log = LoggerFactory.getLogger(MiniGfsController::class.java)
        private val workerMembership = mutableSetOf<String>()
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
}