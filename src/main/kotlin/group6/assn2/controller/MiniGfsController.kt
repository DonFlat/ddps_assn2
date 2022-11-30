package group6.assn2.controller

import group6.assn2.service.MiniGfsClients
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MiniGfsController @Autowired constructor(val miniGfsClients: MiniGfsClients) {

    @Value("nodeSetting.master")
    lateinit var masterNode: String

    @Value("nodeSetting.isMaster")
    lateinit var isMaster: String

    companion object {
        private val log = LoggerFactory.getLogger(MiniGfsController::class.java)
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

    @Scheduled(fixedRate = 5000)
    fun checkMemberShipController() {
        log.info("Checking if registered in master")
        when (miniGfsClients.checkMemberShipService()) {
            true -> log.info("Registered!")
            false -> {
                log.info("Retry registration")
                val retryResult = miniGfsClients.checkMemberShipService()
                log.info("Retried results: $retryResult")
            }
        }
    }
}