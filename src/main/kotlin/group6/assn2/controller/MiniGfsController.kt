package group6.assn2.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MiniGfsController {

    companion object {
        private val log = LoggerFactory.getLogger(MiniGfsController::class.java)
    }

    @GetMapping("/")
    fun hello(): String {
        log.info("Hit Here!")
        return "Hello world!\n"
    }
}