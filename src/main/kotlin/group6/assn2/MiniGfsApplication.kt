package group6.assn2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
class MiniGfsApplication

fun main(args: Array<String>) {
	runApplication<MiniGfsApplication>(*args)
}
