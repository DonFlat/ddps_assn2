package group6.assn2.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Service
@FeignClient(name = "miniGfsClient", url = "http://node102:2206")
interface MiniGfsClients {

    @RequestMapping(method = [RequestMethod.GET], value = ["/"])
    fun getStatus(): String

    @RequestMapping(method = [RequestMethod.POST], value = ["/register-worker/{nodeId}"])
    fun checkWorkerMemberShip(@PathVariable("nodeId") nodeId: String): Boolean
}