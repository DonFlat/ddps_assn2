package group6.assn2.service

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import java.net.URI

@Service
@FeignClient(name = "miniGfsClient", url = "http://node102:2206")
interface MiniGfsClients {

    @RequestMapping(method = [RequestMethod.GET], value = ["/alive"])
    fun checkAlive(baseUri: URI): String

    @RequestMapping(method = [RequestMethod.POST], value = ["/register-worker/{nodeId}"])
    fun checkWorkerMemberShip(baseUri: URI, @PathVariable("nodeId") nodeId: String): Boolean

    @RequestMapping(method = [RequestMethod.POST], value = ["{fileName}/{nodeId}/metadata"])
    fun addFileMetaData(baseUri: URI, @PathVariable("fileName") fileName: String, @PathVariable("nodeId") nodeId: String)
}