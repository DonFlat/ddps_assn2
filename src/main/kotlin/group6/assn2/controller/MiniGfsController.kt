package group6.assn2.controller

import group6.assn2.service.MiniGfsClients
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MiniGfsController @Autowired constructor(val miniGfsClients: MiniGfsClients) {

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
}