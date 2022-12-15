import http from 'k6/http'
import { sleep } from 'k6'

export default function () {

    // Prepare request lease
    const masterNode = 'localhost'
    const fileName = 'hello-there'

    const getLeaseUrl = `http://${masterNode}:2206/${fileName}/lease`

    // Prepare where to write
    const chunkserverNode = 'localhost'
    const replicateNumber = 3
    const writeFileUrl = `http://${chunkserverNode}:2206/file/${fileName}/content/${replicateNumber}`

    const chunkservers = http.get(getLeaseUrl)
    console.log(chunkservers.body)

    // Prepare file content to write
    const content = 'This part sent from the k6 test'

    const writeResponse = http.post(writeFileUrl, content)
}